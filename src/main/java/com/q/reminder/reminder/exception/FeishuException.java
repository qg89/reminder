package com.q.reminder.reminder.exception;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;

import java.util.Arrays;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.exception.FeishuException
 * @Description :
 * @date :  2023.03.29 09:53
 */
public class FeishuException extends RuntimeException {

    public FeishuException() {
        super();
    }

    public FeishuException(Throwable e, String message) {
        super(message, e);
        sendContent(message, e);
    }

    public FeishuException(String message) {
        super(message);
    }

    private void sendContent(String message, Throwable e) {
        JSONObject content = new JSONObject();
        JSONObject all = new JSONObject();
        JSONArray contentJsonArray = new JSONArray();
        all.put("title", message);
        all.put("content", contentJsonArray);
        content.put("zh_cn", all);
        JSONArray atjsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("tag", "text");
        if (e != null) {
            JSONObject errorMsg = new JSONObject();
            errorMsg.put("异常信息 概览", e.getMessage() + "\r\n");
            JSONArray jsonArray = new JSONArray();
            Arrays.stream(e.getStackTrace()).forEach(s -> {
                jsonArray.add(Map.of("className", s.getClassName(), "methodName", s.getMethodName(), "lineNumber", s.getLineNumber()));
            });
            errorMsg.put("异常堆栈", jsonArray);
            json.put("text", errorMsg.toJSONString());
        } else {
            json.put("text", "异常信息为空！");
        }
        atjsonArray.add(json);
        contentJsonArray.add(atjsonArray);
        MessageVo vo = new MessageVo();
        vo.setContent(content.toJSONString());
        vo.setMsgType("post");
        vo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
        vo.setReceiveId("ou_35e03d4d8754dd35fed26c26849c85ab");
        BaseFeishu.message().sendContent(vo);
    }
}
