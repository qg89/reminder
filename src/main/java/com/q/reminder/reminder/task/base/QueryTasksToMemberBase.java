package com.q.reminder.reminder.task.base;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.OverdueTaskHistory;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.AdminInfoService;
import com.q.reminder.reminder.service.OverdueTaskHistoryService;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;
import com.q.reminder.reminder.vo.QueryVo;
import com.q.reminder.reminder.vo.RedmineVo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.log.OmsLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.FeiShuHandle
 * @Description : 过期任务提醒个人，base版本
 * @date :  2022.09.27 08:38
 */
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


    /**
     * @param expiredDay
     * @param noneStatusList
     * @param contentStatus
     * @param log
     */
    public void feiShu(int expiredDay, List<String> noneStatusList, Boolean contentStatus, OmsLogger log) {
        StringBuilder sendAdminContent = new StringBuilder();
        sendAdminContent.append("当日执行情况如下(").append(new DateTime().toString("yyyy-MM-dd")).append("):\r\n");

        // 通过人员查看对应redmine人员关系，并返回redmine姓名和飞书member_id关系
        List<UserMemgerInfo> list = userMemberService.list(Wrappers.<UserMemgerInfo>lambdaQuery().eq(UserMemgerInfo::getResign, 0));
        Map<String, String> memberIds = list.stream().collect(Collectors.toMap(UserMemgerInfo::getName, UserMemgerInfo::getMemberId));

        // 组装数据， 通过人员，获取要发送的内容
        List<RProjectInfo> projectInfos = projectInfoService.listAll().stream().filter(e -> StringUtils.isNotBlank(e.getPmKey())).toList();
        List<AdminInfo> adminInfoList = adminInfoService.list();

        QueryVo vo = new QueryVo();
        vo.setNoneStatusList(noneStatusList);
        vo.setExpiredDay(expiredDay);
        vo.setContainsStatus(contentStatus);
        Map<String, List<RedmineVo>> listMap = RedmineApi.queryUserByExpiredDayList(vo, projectInfos).stream().collect(Collectors.groupingBy(RedmineVo::getAssigneeName));
        if (CollectionUtils.isEmpty(listMap)) {
            sendAdminContent.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("过期人员数量:").append(0).append("\r\n");
            sendAdminContent.append("执行完成!");
            sendAdmin(log, sendAdminContent, adminInfoList);
            return;
        }
        sendAdminContent.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("过期人员数量:").append(listMap.size()).append(" 查询redmine过期人员集合完成!").append("\r\n");
        List<OverdueTaskHistory> historys = new ArrayList<>();
        listMap.forEach((assigneeName, issueList) -> {
            JSONObject con = new JSONObject();
            JSONObject all = new JSONObject();
            con.put("zh_cn", all);
            all.put("title", "【过期任务提醒 (" + DateTime.now().toString("yyyy-MM-dd") + ")】");
            JSONArray contentJsonArray = new JSONArray();
            all.put("content", contentJsonArray);
            String redmineUrl = "";

            String name = assigneeName.replace(" ", "");
            for (RedmineVo issue : issueList) {
                String redmineId = issue.getRedmineId();
                String issueSubject = issue.getSubject();
                String statusName = issue.getStatusName();
                Date dueDate1 = issue.getDueDate();
                String projectName = issue.getProjectName();
                Date updatedOn = issue.getUpdatedOn();
                redmineUrl = issue.getRedmineUrl();
                OverdueTaskHistory history = new OverdueTaskHistory();

                JSONArray subContentJsonObject = new JSONArray();
                JSONObject subject = new JSONObject();
                subject.put("tag", "text");
                subject.put("text", "\r\n任务主题: ");
                subContentJsonObject.add(subject);

                JSONObject a = new JSONObject();
                a.put("tag", "a");
                a.put("href", redmineUrl + "/issues/" + redmineId);
                a.put("text", issueSubject);
                subContentJsonObject.add(a);

                JSONObject task = new JSONObject();
                task.put("tag", "text");
                task.put("text", "\r\n当前任务状态: " + statusName);
                subContentJsonObject.add(task);

                JSONObject assignee = new JSONObject();
                assignee.put("tag", "text");
                assignee.put("text", "\r\n指派给: " + assigneeName);
                subContentJsonObject.add(assignee);

                JSONObject dueDate = new JSONObject();
                dueDate.put("tag", "text");
                dueDate.put("text", "\r\n计划完成日期: " + new DateTime(dueDate1).toString("yyyy-MM-dd"));
                subContentJsonObject.add(dueDate);

                history.setAssigneeName(name);
                history.setProjectName(projectName);
                history.setSubjectName(issueSubject);
                history.setRedmineId(redmineId);
                history.setLastUpdateTime(updatedOn);
                historys.add(history);
                contentJsonArray.add(subContentJsonObject);
            }
            JSONArray subContentJsonArray = new JSONArray();
            JSONObject myTask = new JSONObject();
            myTask.put("tag", "a");
            myTask.put("href", redmineUrl + "/issues?assigned_to_id=me&set_filter=1&sort=priority%3Adesc%2Cupdated_on%3Adesc");
            myTask.put("text", "点击查看指派给我的任务");
            subContentJsonArray.add(myTask);
            contentJsonArray.add(subContentJsonArray);

            String memberId = memberIds.get(name);
            if (StringUtils.isBlank(memberId)) {
                return;
            }
            MessageVo sendVo = new MessageVo();
            sendVo.setContent(con.toJSONString());
            sendVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
            sendVo.setMsgType("post");
            sendVo.setReceiveId(memberId);
            BaseFeishu.message().sendContent(sendVo, log);
            log.info("[过期任务提醒个人]-发送飞书任务, 人员MemberId：{}", sendVo.getReceiveId());
        });

        sendAdminContent.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("发送飞书任务完成!").append("\r\n");
        overdueTaskHistoryService.saveOrUpdateBatch(historys);
        sendAdminContent.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("执行完成!").append("\r\n");
        sendAdmin(log, sendAdminContent, adminInfoList);
        log.info("过期任务提醒个人,执行完成");
    }

    private void sendAdmin(OmsLogger log, StringBuilder contentAll, List<AdminInfo> adminInfoList) {
        adminInfoList.forEach(e -> {
            MessageVo sendVo = new MessageVo();
            sendVo.setReceiveId(e.getMemberId());
            sendVo.setContent(contentAll.toString());
            sendVo.setMsgType("text");
            sendVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
            BaseFeishu.message().sendText(sendVo, log);
        });
    }
}
