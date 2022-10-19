package com.q.reminder.reminder.handle.base;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.OverdueTaskHistory;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.OverdueTaskHistoryService;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.UserGroupService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.QueryRedmineVo;
import com.q.reminder.reminder.vo.SendVo;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.OverdueTasksHandle
 * @Description : 每天9点半提醒，群提醒
 * @date :  2022.09.27 19:13
 */
@Log4j2
@Component
public class OverdueTasksAgainToGroupBase {
    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;
    @Value("${redmine-config.old_url}")
    private String redmineOldUrl;
    @Value("${redmine-config.api-access-key.saiko}")
    private String apiAccessKeySaiko;

    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private OverdueTaskHistoryService overdueTaskHistoryService;
    @Autowired
    private UserGroupService userGroupService;

    public void overdueTasksAgainToGroup(int expiredDay, List<String> noneStatusList, Boolean containStatus) {
        String secret = FeiShuApi.getSecret(appId, appSecret);
        // 组装数据， 通过人员，获取要发送的内容
        List<ProjectInfo> projectInfoList = projectInfoService.list();
        Set<String> projectIds = projectInfoList.stream().map(ProjectInfo::getPId).collect(Collectors.toSet());

        QueryRedmineVo vo = new QueryRedmineVo();
        vo.setProjects(projectIds);
        vo.setNoneStatusList(noneStatusList);
        vo.setApiAccessKey(apiAccessKeySaiko);
        vo.setRedmineUrl(redmineOldUrl);
        vo.setExpiredDay(expiredDay);
        vo.setContainsStatus(containStatus);
        Map<String, List<Issue>> listMap = RedmineApi.queryUserByExpiredDayList(vo);

        // 通过人员查看对应redmine人员关系，并返回redmine姓名和飞书member_id关系
        List<UserMemgerInfo> list = userMemberService.list();
        Map<String, String> memberIds = list.stream().collect(Collectors.toMap(UserMemgerInfo::getName, UserMemgerInfo::getMemberId));

        List<OverdueTaskHistory> historys = new ArrayList<>();
        Map<String, SendVo> sendMap = new HashMap<>();
        JSONArray contentJsonArray = new JSONArray();
        listMap.forEach((k, issueList) -> {
            String name = k.replace(" ", "");
            JSONArray atjsonArray = new JSONArray();
            JSONObject at = new JSONObject();
            at.put("tag", "at");
            at.put("user_id", memberIds.get(name));
            at.put("user_name", name);
            atjsonArray.add(at);
            contentJsonArray.add(atjsonArray);

            issueList.forEach(i -> {
                Integer id = i.getId();
                String subject = i.getSubject();
                String statusName = i.getStatusName();
                Date dueDate = i.getDueDate();
                Date updatedOn = i.getUpdatedOn();
                String projectName = i.getProjectName();
                JSONArray subContentJsonObject = new JSONArray();
                JSONObject subjectJson = new JSONObject();
                subjectJson.put("tag", "text");
                subjectJson.put("text", "任务主题: ");
                subContentJsonObject.add(subjectJson);

                JSONObject a = new JSONObject();
                a.put("tag", "a");
                a.put("href", redmineOldUrl + "issues/" + id);
                a.put("text", subject);
                subContentJsonObject.add(a);

                JSONObject task = new JSONObject();
                task.put("tag", "text");
                task.put("text", "\r\n当前任务状态: " + statusName);
                subContentJsonObject.add(task);

                JSONObject dueDateJson = new JSONObject();
                dueDateJson.put("tag", "text");
                dueDateJson.put("text", "\r\n计划完成日期: " + new DateTime(dueDate).toString("yyyy-MM-dd"));
                subContentJsonObject.add(dueDateJson);
                contentJsonArray.add(subContentJsonObject);

                OverdueTaskHistory history = new OverdueTaskHistory();
                history.setAssigneeName(name);
                history.setProjectName(projectName);
                history.setSubjectName(subject);
                history.setRedmineId(id);
                history.setLastUpdateTime(updatedOn);
                history.setType("1");
                historys.add(history);
            });

            String memberId = memberIds.get(name);
            if (StringUtils.isBlank(memberId)) {
                return;
            }
            SendVo sendVo = new SendVo();
            sendVo.setAssigneeName(name);
            sendVo.setMemberId(memberId);
            sendMap.put(memberId, sendVo);
        });
        LambdaQueryWrapper<UserGroup> lq = new LambdaQueryWrapper<>();
        lq.in(UserGroup::getMemberId, sendMap.keySet());
        List<UserGroup> usergroupList = userGroupService.list();

        JSONObject content = new JSONObject();
        JSONObject all = new JSONObject();
        all.put("title", "【当前任务已过期,提醒公告 (" + DateTime.now().toString("yyyy-MM-dd") + ")");
        all.put("content", contentJsonArray);
        content.put("zh_cn", all);

        usergroupList.stream().map(UserGroup::getChatId).forEach(e -> {
            try {
                FeiShuApi.sendGroupByChats(e, content.toJSONString(), secret);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        boolean overdueTaskHistory = overdueTaskHistoryService.saveOrUpdateBatch(historys);
        if (!overdueTaskHistory) {
            log.error("任务保存历史记录失败!");
            return;
        }
        log.info("过期任务提醒群组,执行完成");
    }
}
