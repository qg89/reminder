package com.q.reminder.reminder.handle;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.*;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.SendVo;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.OverdueTasksHandle
 * @Description : 过期任务提醒群组，每天9点半提醒，为该状态的人员
 * @date :  2022.09.27 19:13
 */
@Log4j2
@Component
public class OverdueTasksToGroupHandle {
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
    private NoneStatusService noneStatusService;
    @Autowired
    private OverdueTaskHistoryService overdueTaskHistoryService;
    @Autowired
    private UserGroupService userGroupService;

    //    @Scheduled(cron = "0 30 9 * * ?")
    @Scheduled(cron = "0/20 * * * * ?")
    public void sendOverdueTask() {
        String secret = FeiShuApi.getSecret(appId, appSecret);
        // 组装数据， 通过人员，获取要发送的内容
        List<ProjectInfo> projectInfoList = projectInfoService.list();
        Set<String> projectIds = projectInfoList.stream().map(ProjectInfo::getPId).collect(Collectors.toSet());

        LambdaQueryWrapper<NoneStatus> qw = new LambdaQueryWrapper<>();
        qw.select(NoneStatus::getNoneStatus);
        List<String> noneStatusList = noneStatusService.listObjs(qw, (Object::toString));

        Map<String, List<Issue>> listMap = RedmineApi.queryUserList(projectIds, noneStatusList, apiAccessKeySaiko, redmineOldUrl);

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
                JSONArray subContentJsonObject = new JSONArray();
                JSONObject subject = new JSONObject();
                subject.put("tag", "text");
                subject.put("text", "任务主题: ");
                subContentJsonObject.add(subject);

                JSONObject a = new JSONObject();
                a.put("tag", "a");
                a.put("href", redmineOldUrl + "issues/" + id);
                a.put("text", i.getSubject());
                subContentJsonObject.add(a);

                JSONObject task = new JSONObject();
                task.put("tag", "text");
                task.put("text", "\r\n当前任务状态: " + i.getStatusName());
                subContentJsonObject.add(task);

                JSONObject dueDate = new JSONObject();
                dueDate.put("tag", "text");
                dueDate.put("text", "\r\n计划完成日期: " + new DateTime(i.getDueDate()).toString("yyyy-MM-dd"));
                subContentJsonObject.add(dueDate);
                contentJsonArray.add(subContentJsonObject);

                OverdueTaskHistory history = new OverdueTaskHistory();
                history.setAssigneeName(name);
                history.setProjectName(i.getProjectName());
                history.setSubjectName(i.getSubject());
                history.setRedmineId(id);
                history.setLastUpdateTime(i.getUpdatedOn());
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
