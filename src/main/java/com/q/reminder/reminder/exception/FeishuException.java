package com.q.reminder.reminder.exception;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.constant.FeiShuContents;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;

import java.util.Arrays;
import java.util.LinkedHashMap;

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
            JSONArray jsonArray = new JSONArray();
            errorMsg.put("异常信息 概览", e.getMessage() + "\r\n");
            Arrays.stream(e.getStackTrace()).forEach(s -> {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("className", s.getClassName());
                map.put("methodName", s.getMethodName());
                map.put("lineNumber", s.getLineNumber());
                jsonArray.add(map);
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
        vo.setReceiveId(FeiShuContents.ADMIN_MEMBERS);
        BaseFeishu.message().sendContent(vo);
    }
}
