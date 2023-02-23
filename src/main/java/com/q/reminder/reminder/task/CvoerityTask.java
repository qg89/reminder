package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.constant.MsgTypeConstants;
import com.q.reminder.reminder.entity.CoverityLog;
import com.q.reminder.reminder.service.CoverityLogService;
import com.q.reminder.reminder.service.CoverityService;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.util.CoverityApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.ChatProjectVo;
import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import com.q.reminder.reminder.vo.MessageVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.CvoerityTask
 * @Description :
 * @date :  2022.12.01 10:37
 */
@Log4j2
@Component
public class CvoerityTask {
    @Autowired
    private CoverityService coverityService;
    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private Client client;
    @Autowired
    private CoverityLogService coverityLogService;

    @XxlJob("coverity")
    public ReturnT<String> coverity() {
        String pKey = XxlJobHelper.getJobParam();
        ReturnT<String> returnT = new ReturnT<>(null);
        List<CoverityAndRedmineSaveTaskVo> list = coverityService.queryByProject(pKey);
        String weekOfYear = DateTime.now().toString("yy") + "W" + (DateUtil.thisWeekOfYear() - 1);
        JSONArray subContentJsonArray = new JSONArray();
        Map<String, ChatProjectVo> projectVoMap = groupInfoService.listByProect(pKey).stream().collect(Collectors.toMap(ChatProjectVo::getChatId, Function.identity(), (v1, v2) -> v1));

        Map<String, JSONArray> arrayMap = new LinkedHashMap<>();

        List<CoverityLog> coverityLogList = new ArrayList<>();
        list.forEach(vo -> {
            String assigneeId = vo.getAssigneeId().toString();
            CoverityAndRedmineSaveTaskVo coverityAndRedmineSaveTaskVo = CoverityApi.readCoverity(vo);
            List<CoverityLog> coverityLogs = coverityAndRedmineSaveTaskVo.getCoverityLogs();
            if (CollectionUtils.isEmpty(coverityLogs)) {
                returnT.setMsg(returnT.getMsg() + vo.getRedmineProjectName() + "-中、高问题报告-为空;    ");
                return;
            }
            coverityLogList.addAll(coverityLogs);
            try {
                coverityAndRedmineSaveTaskVo.setSubject(vo.getRedmineProjectName() + "-中、高问题报告-" + weekOfYear + "\r\n");
                RedmineApi.saveTask(coverityAndRedmineSaveTaskVo);
            } catch (Exception e) {
                returnT.setCode(500);
                returnT.setContent(e.getMessage());
            }
            Integer coverityNo = coverityAndRedmineSaveTaskVo.getCoverityNo();
            if (coverityNo != null && coverityNo > 0) {
                JSONObject content = new JSONObject();
                content.put("tag", "text");
                content.put("text", "  " + vo.getRedmineProjectName() + "-问题数量：" + coverityNo + "\r\n");

                JSONObject at = new JSONObject();
                at.put("tag", "at");
                at.put("user_id", vo.getMemberId());
                at.put("user_name", vo.getName());
                subContentJsonArray.add(at);
                subContentJsonArray.add(content);
            }

            JSONArray jsonArray = arrayMap.get(assigneeId);
            if (jsonArray != null) {
                jsonArray.addAll(subContentJsonArray);
            } else {
                arrayMap.put(assigneeId, subContentJsonArray);
            }
        });
        sendGroups(subContentJsonArray, projectVoMap);
        expiredTask(projectVoMap, coverityLogList);
        coverityLogService.saveBatch(coverityLogList);
        return returnT;
    }

    /**
     * 过期任务
     *
     * @param projectVoMap
     * @param coverityLogList
     */
    private void expiredTask(Map<String, ChatProjectVo> projectVoMap, List<CoverityLog> coverityLogList) {
        Map<String, List<CoverityLog>> coverityLogs = coverityLogList.stream().filter(e -> DateUtil.betweenDay(e.getFirstDate(), new Date(), true) > 4).
                collect(Collectors.groupingBy(CoverityLog::getAssigneeId));
        boolean over = CollectionUtils.isEmpty(coverityLogs);
        if (over) {
            return;
        }
        projectVoMap.forEach((chatId, projectInfo) -> {
            List<CoverityLog> logList = coverityLogs.get(projectInfo.getAssigneeId());
            if (CollectionUtils.isEmpty(logList)) {
                return;
            }
            int index = logList.size();
            JSONObject con = new JSONObject();
            JSONObject all = new JSONObject();
            JSONArray contentJsonArray = new JSONArray();
            JSONArray subContentJsonArray = new JSONArray();
            con.put("zh_cn", all);
            all.put("title", "【警告！！！据上周检查结果至今未修复，通报 (" + DateUtil.today() + ")】");
            all.put("content", contentJsonArray);
            createSubContentJson(projectInfo, subContentJsonArray);
            contentJsonArray.add(subContentJsonArray);

            JSONObject expired = new JSONObject();
            expired.put("tag", "at");
            expired.put("user_id", projectInfo.getMemberId());
            expired.put("user_name", projectInfo.getName());

            JSONObject text = new JSONObject();
            text.put("tag", "text");
            text.put("text", "\r\n未处理数量:【" + index + "】 \r\nCID：" + StringUtils.joinWith(",", logList.stream().map(CoverityLog::getCId).collect(Collectors.toSet())) + "\r\n请检查coverity对应问题进行修复");
            JSONObject line = new JSONObject();
            line.put("tag", "text");
            line.put("text", "\r\n———————————————————————————————————————————————————");
            subContentJsonArray.add(text);
            subContentJsonArray.add(expired);
            subContentJsonArray.add(line);

            MessageVo messageVo = new MessageVo();
            messageVo.setContent(con.toJSONString());
            messageVo.setReceiveId(chatId);
            messageVo.setMsgType(MsgTypeConstants.POST);
            messageVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.CHAT_ID);
            try {
                BaseFeishu.message(client).sendContent(messageVo);
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private void sendGroups(JSONArray subContentJsonArray, Map<String, ChatProjectVo> projectVoMap) {
        projectVoMap.forEach((chatId, projectInfo) -> {
            JSONObject con = new JSONObject();
            JSONObject all = new JSONObject();
            JSONArray contentJsonArray = new JSONArray();
            con.put("zh_cn", all);
            all.put("title", "【Coverity 创建中、高问题，提醒 (" + DateUtil.now() + ")】");
            all.put("content", contentJsonArray);
            createSubContentJson(projectInfo, subContentJsonArray);
            contentJsonArray.add(subContentJsonArray);

            MessageVo messageVo = new MessageVo();
            messageVo.setContent(con.toJSONString());
            messageVo.setReceiveId(chatId);
            messageVo.setMsgType(MsgTypeConstants.POST);
            messageVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.CHAT_ID);
            try {
                BaseFeishu.message(client).sendContent(messageVo);
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private void createSubContentJson(ChatProjectVo groupInfo, JSONArray subContentJsonArray) {
        if (subContentJsonArray.size() > 0) {
            JSONObject line = new JSONObject();
            line.put("tag", "text");
            line.put("text", "\r\n———————————————————————————————————————————————————");
            JSONObject myTask = new JSONObject();
            myTask.put("tag", "a");
            myTask.put("href", "http://redmine-qa.mxnavi.com/issues?assigned_to_id=me&set_filter=1&sort=priority%3Adesc%2Cupdated_on%3Adesc");
            myTask.put("text", "\n\r查看指派给我的任务");
            subContentJsonArray.add(myTask);
            subContentJsonArray.add(line);
        } else {
            JSONObject none = new JSONObject();
            none.put("tag", "text");
            none.put("text", groupInfo.getReminderNone());
            subContentJsonArray.add(none);
        }
    }
}
