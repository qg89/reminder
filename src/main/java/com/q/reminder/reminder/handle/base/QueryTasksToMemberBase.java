package com.q.reminder.reminder.handle.base;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.OverdueTaskHistory;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.QueryVo;
import com.q.reminder.reminder.vo.SendVo;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.FeiShuHandle
 * @Description : 过期任务提醒个人，base版本
 * @date :  2022.09.27 08:38
 */
@Log4j2
@Component
public class QueryTasksToMemberBase {

    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private OverdueTaskHistoryService overdueTaskHistoryService;
    @Autowired
    private AdminInfoService adminInfoService;

    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;
    @Value("${redmine-config.old_url}")
    private String redmineOldUrl;
    @Value("${redmine-config.api-access-key.saiko}")
    private String apiAccessKeySaiko;


    /**
     *  @param expiredDay
     * @param noneStatusList
     * @param contentStatus
     */
    public void feiShu(int expiredDay, List<String> noneStatusList, Boolean contentStatus) {
        String authorization = FeiShuApi.getSecret(appId, appSecret);
        StringBuilder contentAll = new StringBuilder();
        contentAll.append("当日执行情况如下(").append(new DateTime().toString("yyyy-MM-dd")).append("):\r\n");

        // 通过人员查看对应redmine人员关系，并返回redmine姓名和飞书member_id关系
        List<UserMemgerInfo> list = userMemberService.list();
        Map<String, String> memberIds = list.stream().collect(Collectors.toMap(UserMemgerInfo::getName, UserMemgerInfo::getMemberId));

        // 组装数据， 通过人员，获取要发送的内容
        List<ProjectInfo> projectInfoList = projectInfoService.list();
        Set<String> projectIds = projectInfoList.stream().map(ProjectInfo::getPKey).collect(Collectors.toSet());

        QueryVo vo = new QueryVo();
        vo.setProjects(projectIds);
        vo.setNoneStatusList(noneStatusList);
        vo.setApiAccessKey(apiAccessKeySaiko);
        vo.setRedmineUrl(redmineOldUrl);
        vo.setExpiredDay(expiredDay);
        vo.setContainsStatus(contentStatus);
        Map<String, List<Issue>> listMap = RedmineApi.queryUserByExpiredDayList(vo);
        if (CollectionUtils.isEmpty(listMap)) {
            contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("过期人员数量:").append(listMap.size()).append("\r\n");
            contentAll.append("执行完成!");
            sendAdmin(contentAll.toString(), authorization);
            return;
        }
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("过期人员数量:").append(listMap.size()).append(" 查询redmine过期人员集合完成!").append("\r\n");
        // key: member_id, value: content
        Map<String, SendVo> sendMap = new HashMap<>();
        List<OverdueTaskHistory> historys = new ArrayList<>();

        listMap.forEach((k, issueList) -> {
            JSONObject con = new JSONObject();
            JSONObject all = new JSONObject();
            con.put("zh_cn", all);
            all.put("title", "【过期任务提醒 (" + DateTime.now().toString("yyyy-MM-dd") + ")】");
            JSONArray contentJsonArray = new JSONArray();
            all.put("content", contentJsonArray);

            String name = k.replace(" ", "");
            issueList.forEach(i -> {
                Integer id = i.getId();
                String assigneeName = i.getAssigneeName();
                OverdueTaskHistory history = new OverdueTaskHistory();

                JSONArray subContentJsonObject = new JSONArray();
                JSONObject subject = new JSONObject();
                subject.put("tag", "text");
                subject.put("text", "\r\n任务主题: ");
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

                JSONObject assignee = new JSONObject();
                assignee.put("tag", "text");
                assignee.put("text", "\r\n指派给: " + assigneeName);
                subContentJsonObject.add(assignee);

                JSONObject dueDate = new JSONObject();
                dueDate.put("tag", "text");
                dueDate.put("text", "\r\n计划完成日期: " + new DateTime(i.getDueDate()).toString("yyyy-MM-dd"));
                subContentJsonObject.add(dueDate);

                history.setAssigneeName(name);
                history.setProjectName(i.getProjectName());
                history.setSubjectName(i.getSubject());
                history.setRedmineId(id);
                history.setLastUpdateTime(i.getUpdatedOn());
                historys.add(history);
                contentJsonArray.add(subContentJsonObject);
            });
            JSONArray subContentJsonArray = new JSONArray();
            JSONObject myTask = new JSONObject();
            myTask.put("tag", "a");
            myTask.put("href", "http://redmine-qa.mxnavi.com/issues?assigned_to_id=me&set_filter=1&sort=priority%3Adesc%2Cupdated_on%3Adesc");
            myTask.put("text", "点击查看指派给我的任务");
            subContentJsonArray.add(myTask);
            contentJsonArray.add(subContentJsonArray);

            String memberId = memberIds.get(name);
            if (StringUtils.isBlank(memberId)) {
                return;
            }
            SendVo sendVo = new SendVo();
            sendVo.setContent(con.toJSONString());
            sendVo.setAssigneeName(name);
            sendVo.setMemberId(memberId);
            sendMap.put(memberId, sendVo);
        });
        if (CollectionUtils.isEmpty(sendMap)) {
            contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("当日暂无过期任务!").append("\r\n");
        }
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("发送飞书任务开始!").append("\r\n");
        sendMap.forEach((k, v) -> {
            try {
                FeiShuApi.sendPost(v, authorization, contentAll);
            } catch (IOException e) {
                log.error("", e);
            }
        });
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("发送飞书任务完成!").append("\r\n");
        overdueTaskHistoryService.saveOrUpdateBatch(historys);
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("执行完成!").append("\r\n");
        sendAdmin(contentAll.toString(), authorization);
        log.info("过期任务提醒个人,执行完成");
    }

    void sendAdmin(String content, String secret) {
        List<AdminInfo> adminInfos = adminInfoService.list();
        adminInfos.forEach(e -> {
            try {
                FeiShuApi.sendText(e.getMemberId(), content, secret);
            } catch (IOException ex) {
                log.error("管理员任务发送失败 {}", e);
            }
        });
    }
}
