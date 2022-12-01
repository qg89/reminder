package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.contents.MsgTypeContents;
import com.q.reminder.reminder.service.CoverityService;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.util.CoverityApi;
import com.q.reminder.reminder.util.FeishuJavaUtils;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.ChatProjectVo;
import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import com.q.reminder.reminder.vo.MessageVo;
import com.taskadapter.redmineapi.bean.Issue;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @XxlJob("coverity")
    public ReturnT<String> coverity() {
        String pKey = XxlJobHelper.getJobParam();
        ReturnT<String> returnT = new ReturnT<>(null);
        List<CoverityAndRedmineSaveTaskVo> list = coverityService.queryByProject(pKey);
        String weekOfYear = DateTime.now().toString("yy") + "W" + (DateUtil.thisWeekOfYear() - 1);
        JSONArray subContentJsonArray = new JSONArray();
        Map<String, ChatProjectVo> projectVoMap = groupInfoService.listByProect(pKey).stream().collect(Collectors.toMap(ChatProjectVo::getChatId, Function.identity(), (v1, v2) -> v1));

        Map<String, JSONArray> arrayMap = new LinkedHashMap<>();

        list.forEach(vo -> {
            String assigneeId = vo.getAssigneeId().toString();
            Issue issue = null;
            CoverityAndRedmineSaveTaskVo coverityAndRedmineSaveTaskVo = CoverityApi.readCoverity(vo);
            try {
                coverityAndRedmineSaveTaskVo.setSubject(vo.getRedmineProjectName() + "-中、高问题报告-" + weekOfYear + "\r\n");
                issue = RedmineApi.saveTask(coverityAndRedmineSaveTaskVo);
            } catch (Exception e) {
                returnT.setCode(500);
                returnT.setContent(e.getMessage());
            }
            Integer coverityNo = coverityAndRedmineSaveTaskVo.getCoverityNo();
            if (coverityNo > 0) {
                JSONObject content = new JSONObject();
                content.put("tag", "text");
                content.put("text", vo.getRedmineProjectName() + "-问题数量：" + coverityNo);
                subContentJsonArray.add(content);
            }

            JSONArray jsonArray = arrayMap.get(assigneeId);
            if (jsonArray != null) {
                jsonArray.addAll(subContentJsonArray);
            } else {
                arrayMap.put(assigneeId, subContentJsonArray);
            }
        });
        projectVoMap.forEach((chatId, projectInfo) -> {
            MessageVo messageVo = new MessageVo();
            messageVo.setContent(send(projectInfo, subContentJsonArray));
            messageVo.setReceiveId(chatId);
            messageVo.setMsgType(MsgTypeContents.POST);
            messageVo.setClient(client);
            messageVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.CHAT_ID);
            try {
                FeishuJavaUtils.sendContent(messageVo);
            } catch (Exception e) {
                log.error(e);
            }
        });
        return returnT;
    }

    private String send(ChatProjectVo groupInfo, JSONArray subContentJsonArray) {
        JSONObject con = new JSONObject();
        JSONObject all = new JSONObject();
        JSONArray contentJsonArray = new JSONArray();
        con.put("zh_cn", all);
        all.put("title", "【Coverity 创建中、高问题，提醒 (" + DateUtil.now() + ")】");
        all.put("content", contentJsonArray);

        if (subContentJsonArray.size() > 0) {
            JSONObject line = new JSONObject();
            line.put("tag", "text");
            line.put("text", "\r\n———————————————————————————————————————————————————");
            JSONObject myTask = new JSONObject();
            myTask.put("tag", "a");
            myTask.put("href", "http://redmine-qa.mxnavi.com/issues?assigned_to_id=me&set_filter=1&sort=priority%3Adesc%2Cupdated_on%3Adesc");
            myTask.put("text", "\n\r查看指派给我的任务");

            JSONObject at = new JSONObject();
            at.put("tag", "at");
            at.put("user_id", groupInfo.getMemberId());
            at.put("user_name", groupInfo.getName());
            subContentJsonArray.add(myTask);
            subContentJsonArray.add(at);
            subContentJsonArray.add(line);
            contentJsonArray.add(subContentJsonArray);
        } else {
            JSONObject none = new JSONObject();
            none.put("tag", "text");
            none.put("text", groupInfo.getReminderNone());
            subContentJsonArray.add(none);
        }

        return con.toJSONString();
    }
}
