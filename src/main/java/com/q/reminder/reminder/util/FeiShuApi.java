package com.q.reminder.reminder.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.test.T
 * @Description :
 * @date :  2022.09.23 18:45
 */
@Log4j2
public class FeiShuApi {
    private final static String SEND_URL = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=open_id";
    private final static String SEND_GROUP_URL = "https://open.feishu.cn/open-apis/im/v1/messages?receive_id_type=chat_id";
    private final static String TOKEN_URL = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
    private final static String MEMBERS_URL = "https://open.feishu.cn/open-apis/im/v1/chats/%s/members";
    private final static String CHATS_URL = "https://open.feishu.cn/open-apis/im/v1/chats";

    /**
     * 通过人员ID 发送消息
     *
     * @param receiveId
     * @param contentStr
     * @throws IOException
     */
    public static void send(String receiveId, String contentStr, String security) throws IOException {
        JSONObject content = new JSONObject();
        JSONObject text = new JSONObject();
        text.put("text", contentStr);
        content.put("receive_id", receiveId);
        content.put("msg_type", "text");
        content.put("content", text.toJSONString());
        content.put("uuid", UUID.randomUUID());
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(content.toJSONString(), mediaType);
        Request request = new Request.Builder().url(SEND_URL).method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", security)
                .build();
        Response response = client.newCall(request).execute();
        log.info("消息发送状态:{}, receive_id:{}", response.code(), receiveId);
    }

    /**
     * 通过人员ID 发送消息 到指定群
     *
     * @param chatId
     * @param contentStr
     * @param security
     * @throws IOException
     */
    public static void sendGroupByChats(String chatId, String contentStr, String security) throws IOException {
        JSONObject content = new JSONObject();
        JSONObject text = new JSONObject();
        text.put("text", contentStr);
        content.put("receive_id", chatId);
        content.put("msg_type", "text");
        content.put("content", text.toJSONString());
        content.put("uuid", UUID.randomUUID());
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(content.toJSONString(), mediaType);
        Request request = new Request.Builder().url(SEND_GROUP_URL).method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", security)
                .build();
        Response response = client.newCall(request).execute();
        log.info("消息发送状态:{}, receive_id:{}", response.code(), chatId);
    }

    /**
     * 获取授权密钥
     *
     * @return
     */
    public static String getSecret(String appId, String security) {
        JSONObject content = new JSONObject();
        content.put("app_id", appId);
        content.put("app_secret", security);
        String post = HttpUtil.post(TOKEN_URL, content);
        String key = JSON.parseObject(post).getString("tenant_access_token");
        if (StringUtils.isNotBlank(key)) {
            return "Bearer " + key;
        }
        return null;
    }

    /**
     * 通过机器人获取所在群chatId
     */
    public static List<GroupInfo> getGroupToChats(String security) {
        String post = HttpUtil.createGet(CHATS_URL).addHeaders(Map.of("Authorization", security)).execute().body();
        JSONObject jsonObject = JSON.parseObject(post);
        if (jsonObject.getInteger("code") != 0) {
            String msg = jsonObject.getString("msg");
            log.error("获取机器人所在群组失败,{}", msg);
            return null;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        List<GroupInfo> object = JSON.parseArray(data.getJSONArray("items").toJSONString(), GroupInfo.class);
        return object;
    }

    /**
     * 通过chat_id 获取人员open_id
     *
     * @param chats
     */
    public static List<UserMemgerInfo> getMembersByChats(List<GroupInfo> chats, String security, List<UserGroup> userGroupList) {
        List<UserMemgerInfo> lists = new ArrayList<>();
        chats.forEach(chat -> {
            String result = HttpUtil.createGet(String.format(MEMBERS_URL, chat.getChatId())).addHeaders(Map.of("Authorization", security)).execute().body();
            JSONObject jsonObject = JSON.parseObject(result, JSONObject.class);
            if (jsonObject.getInteger("code") != 0) {
                String msg = jsonObject.getString("msg");
                log.error("获取人员失败,{}", msg);
                return;
            }
            List<UserMemgerInfo> items = JSON.parseArray(jsonObject.getJSONObject("data").getJSONArray("items").toJSONString(), UserMemgerInfo.class);
            lists.addAll(items);
            items.forEach(e -> {
                UserGroup ug = new UserGroup();
                ug.setChatId(chat.getChatId());
                ug.setMemberId(e.getMemberId());
                userGroupList.add(ug);
            });
        });
        return lists;
    }

}
