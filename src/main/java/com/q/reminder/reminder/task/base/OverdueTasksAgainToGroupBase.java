package com.q.reminder.reminder.task.base;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.OverdueTaskHistory;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.AdminInfoService;
import com.q.reminder.reminder.service.OverdueTaskHistoryService;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.RedmineVo;
import com.q.reminder.reminder.vo.QueryVo;
import com.q.reminder.reminder.vo.SendUserByGroupVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
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

    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private OverdueTaskHistoryService overdueTaskHistoryService;
    @Autowired
    private AdminInfoService adminInfoService;
    @Autowired
    private FeishuProperties feishuProperties;

    /**
     * 无任务提醒
     *
     * @param contentJsonArray content中第一层集合
     */
    private static void noneOverdueTask(JSONArray contentJsonArray) {
        JSONArray subContentJsonArray = new JSONArray();
        JSONObject noneLine = new JSONObject();
        noneLine.put("tag", "text");
        noneLine.put("text", "%s");
        subContentJsonArray.add(noneLine);
        contentJsonArray.add(subContentJsonArray);
    }

    /**
     * 过期任务发送群提醒
     *
     * @param vo
     */
    public void overdueTasksAgainToGroup(QueryVo vo) {
        List<OverdueTaskHistory> historys = new ArrayList<>();
        String redminderType = vo.getRedminderType();
        String secret = FeiShuApi.getSecret(feishuProperties.getAppId(), feishuProperties.getAppSecret());
        // 组装数据， 通过人员，获取要发送的内容
        List<ProjectInfo> projectInfoList = projectInfoService.list();
        List<RedmineVo> issueUserList = RedmineApi.queryUserByExpiredDayList(vo, projectInfoList);

        issueUserList.forEach(e -> {
            OverdueTaskHistory history = new OverdueTaskHistory();
            String assigneeName = e.getAssigneeName();
            history.setAssigneeName(assigneeName);
            if (StringUtils.isBlank(assigneeName)) {
                history.setAssigneeName(e.getAuthorName());
            }
            history.setProjectName(e.getProjectName());
            history.setSubjectName(e.getSubject());
            history.setRedmineId(e.getRedmineId());
            history.setLastUpdateTime(e.getUpdatedOn());
            history.setType("1");
            historys.add(history);
        });

        // 查询要群对应的人员信息
        List<SendUserByGroupVo> sendUserByGroupVoList = userMemberService.queryUserGroupList();
        // 群内人员
        Set<String> sendUsers = sendUserByGroupVoList.stream().map(SendUserByGroupVo::getAssigneeId).collect(Collectors.toSet());
        // 是否有过期任务
        boolean overdueTask = CollectionUtils.isEmpty(issueUserList);

        JSONArray contentJsonArray = new JSONArray();
        if (!overdueTask) {
            // 处理不在群内的成员
            issueUserList.removeIf(e -> !sendUsers.contains(e.getAssigneeId()));
            if (issueUserList.isEmpty()) {
                log.info("群发送,过期任务人员为空!");
                overdueTask = true;
            } else {
                Map<String, List<RedmineVo>> assigneeMap = issueUserList.stream().collect(Collectors.groupingBy(RedmineVo::getAssigneeName));
                Map<String, List<RedmineVo>> noneAssigneeMap = issueUserList.stream().collect(Collectors.groupingBy(RedmineVo::getAuthorName));
                if (CollectionUtils.isEmpty(assigneeMap)) {
                    assigneeMap = noneAssigneeMap;
                } else {
                    assigneeMap.forEach((k, v) -> {
                        noneAssigneeMap.forEach((k1, v1) -> {
                            if (k.equals(k1)) {
                                v.addAll(v1);
                            }
                        });
                    });
                }
                contentJsonArray = extracted(assigneeMap);
            }
        }

        Map<String, SendUserByGroupVo> sendUserByGroupVoMap = sendUserByGroupVoList.stream().collect(Collectors.toMap(SendUserByGroupVo::getChatId, Function.identity(), (v1, v2) -> v1));

        List<AdminInfo> adminInfoList = adminInfoService.list();
        for (Map.Entry<String, SendUserByGroupVo> map : sendUserByGroupVoMap.entrySet()) {
            JSONObject content = new JSONObject();
            SendUserByGroupVo groupInfo = map.getValue();
            if (overdueTask) {
                noneOverdueTask(contentJsonArray);
                contentJsonArray= JSONArray.parseArray(String.format(contentJsonArray.toJSONString(), groupInfo.getReminderNone()));
            } else {
                content = JSONObject.parseObject(String.format(content.toJSONString(), groupInfo.getReminderNone()));
            }
            JSONObject all = new JSONObject();
            all.put("title", groupInfo.getReminderTitle() + (StringUtils.isBlank(redminderType) ? "" : redminderType));
            all.put("content", contentJsonArray);
            content.put("zh_cn", all);
            try {
                FeiShuApi.sendGroupByChats(map.getKey(), content.toJSONString(), secret);
            } catch (IOException ex) {
                FeiShuApi.sendAdmin(adminInfoList, "过期任务提醒群组,发送异常", secret);
            }
        }

        if (!overdueTask) {
            if (!overdueTaskHistoryService.saveOrUpdateBatch(historys)) {
                FeiShuApi.sendAdmin(adminInfoList, "任务保存历史记录失败！", secret);
                return;
            }
        }
        log.info("过期任务提醒群组,执行完成");
    }

    /**
     * 发送消息组装数据
     *
     * @param listMap
     */
    private JSONArray extracted(Map<String, List<RedmineVo>> listMap) {
        // 通过人员查看对应redmine人员关系，并返回redmine姓名和飞书member_id关系
        List<UserMemgerInfo> list = userMemberService.list();
        Map<String, String> memberIds = list.stream().collect(Collectors.toMap(UserMemgerInfo::getName, UserMemgerInfo::getMemberId));
        JSONArray contentJsonArray = new JSONArray();
        listMap.forEach((k, issueList) -> {
            String name = k.replace(" ", "");
            JSONArray atjsonArray = new JSONArray();
            JSONObject at = new JSONObject();
            at.put("tag", "at");
            at.put("user_id", memberIds.get(name));
            at.put("user_name", name);
            atjsonArray.add(at);

            JSONObject taskSizeJson = new JSONObject();
            taskSizeJson.put("tag", "text");
            taskSizeJson.put("text", " 过期任务数量:【" + issueList.size() + "】");
            atjsonArray.add(taskSizeJson);
            String redmineUrl = "";
            JSONArray subContentJsonArray = new JSONArray();
            for (RedmineVo issue : issueList) {
                String id = issue.getRedmineId();
                String subject = issue.getSubject();
                Date updatedOn = issue.getUpdatedOn();
                String projectName = issue.getProjectName();
                redmineUrl = issue.getRedmineUrl();

                JSONObject a = new JSONObject();
                a.put("tag", "a");
                a.put("href", redmineUrl + "/issues/" + id);
                if (StringUtils.isBlank(issue.getAssigneeName())) {
                    subject = "【未指派人员】-" + subject;
                }
                a.put("text", "\r\n\t" + subject);
                subContentJsonArray.add(a);
            }

            contentJsonArray.add(atjsonArray);
            contentJsonArray.add(subContentJsonArray);

            JSONArray subNoneContentJsonObject = new JSONArray();
            JSONObject line = new JSONObject();
            line.put("tag", "text");
            line.put("text", "\r\n———————————————————————————————————————————————————");
            subNoneContentJsonObject.add(line);
            JSONObject myTask = new JSONObject();
            myTask.put("tag", "a");
            myTask.put("href", redmineUrl + "/issues?assigned_to_id=me&set_filter=1&sort=priority%3Adesc%2Cupdated_on%3Adesc");
            myTask.put("text", "\r\n查看指派给我的任务");
            subNoneContentJsonObject.add(myTask);
            contentJsonArray.add(subNoneContentJsonObject);
        });
        return contentJsonArray;
    }
}
