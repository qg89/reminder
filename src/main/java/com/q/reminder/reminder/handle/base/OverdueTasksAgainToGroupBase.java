package com.q.reminder.reminder.handle.base;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.OverdueTaskHistory;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.OverdueTaskHistoryService;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.QueryRedmineVo;
import com.q.reminder.reminder.vo.QueryVo;
import com.q.reminder.reminder.vo.SendUserByGroupVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;


    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private OverdueTaskHistoryService overdueTaskHistoryService;

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
        String secret = FeiShuApi.getSecret(appId, appSecret);
        // 组装数据， 通过人员，获取要发送的内容
        List<ProjectInfo> projectInfoList = projectInfoService.list();
        List<QueryRedmineVo> issueUserList = RedmineApi.queryUserByExpiredDayList(vo, projectInfoList);

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
                contentJsonArray = extracted(issueUserList.stream().collect(Collectors.groupingBy(QueryRedmineVo::getAssigneeName)), historys);
            }
        }

        Map<String, SendUserByGroupVo> sendUserByGroupVoMap = sendUserByGroupVoList.stream().collect(Collectors.toMap(SendUserByGroupVo::getChatId, Function.identity(), (v1, v2) -> v1));

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
                log.error("过期任务提醒群组,发送异常");
            }
        }

        if (!overdueTask) {
            if (!overdueTaskHistoryService.saveOrUpdateBatch(historys)) {
                log.error("任务保存历史记录失败!");
                return;
            }
        }
        log.info("过期任务提醒群组,执行完成");
    }

    /**
     * 发送消息组装数据
     *
     * @param listMap
     * @param historys
     */
    private JSONArray extracted(Map<String, List<QueryRedmineVo>> listMap, List<OverdueTaskHistory> historys) {
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
            taskSizeJson.put("text", " 过期任务数量:【" + issueList.size() + "】 ==> ");
            atjsonArray.add(taskSizeJson);
            String redmineUrl = "";
            JSONArray subContentJsonArray = new JSONArray();
            for (QueryRedmineVo issue : issueList) {
                String id = issue.getRedmineId();
                String subject = issue.getSubject();
                Date updatedOn = issue.getUpdatedOn();
                String projectName = issue.getProjectName();
                redmineUrl = issue.getRedmineUrl();

                JSONObject a = new JSONObject();
                a.put("tag", "a");
                a.put("href", redmineUrl + "/issues/" + id);
                a.put("text", subject);
                subContentJsonArray.add(a);

                JSONObject noneLine = new JSONObject();
                noneLine.put("tag", "text");
                noneLine.put("text", "\r\n\t");
                subContentJsonArray.add(noneLine);

                OverdueTaskHistory history = new OverdueTaskHistory();
                history.setAssigneeName(name);
                history.setProjectName(projectName);
                history.setSubjectName(subject);
                history.setRedmineId(Integer.valueOf(id));
                history.setLastUpdateTime(updatedOn);
                history.setType("1");
                historys.add(history);
            }
            JSONObject myTask = new JSONObject();
            myTask.put("tag", "a");
            myTask.put("href", redmineUrl + "/issues?assigned_to_id=me&set_filter=1&sort=priority%3Adesc%2Cupdated_on%3Adesc");
            myTask.put("text", "查看指派给我的任务");
            atjsonArray.add(myTask);

            contentJsonArray.add(atjsonArray);
            contentJsonArray.add(subContentJsonArray);

            JSONArray subNoneContentJsonObject = new JSONArray();
            JSONObject line = new JSONObject();
            line.put("tag", "text");
            line.put("text", "\r------------------------------------------------");
            subNoneContentJsonObject.add(line);
            contentJsonArray.add(subNoneContentJsonObject);
        });
        return contentJsonArray;
    }
}
