package com.q.reminder.reminder.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.vo.SendVo;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final static String MEMBERS_URL = "https://open.feishu.cn/open-apis/im/v1/chats/%s/members?page_size=100";
    private final static String MEMBERS_PAGE_URL = MEMBERS_URL + "&page_token=%s";
    private final static String CHATS_URL = "https://open.feishu.cn/open-apis/im/v1/chats";

    /**
     * 通过人员ID 发送消息
     *
     * @param vo
     * @param authorization
     * @param contentAll
     * @throws IOException
     */
    public static void sendPost(SendVo vo, String authorization, StringBuilder contentAll) throws IOException {
        JSONObject requestParam = new JSONObject();
        String receiveId = vo.getMemberId();
        String content = vo.getContent();
        requestParam.put("receive_id", receiveId);
        requestParam.put("msg_type", "post");
        requestParam.put("content", content);
        requestParam.put("uuid", UUID.randomUUID());

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(requestParam.toJSONString(), mediaType);
        Request request = new Request.Builder().url(SEND_URL).method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", authorization)
                .build();
        try (Response response = client.newCall(request).execute();) {
            contentAll.append("消息发送状态:").append("指派人员:").append(vo.getAssigneeName()).append(", 飞书返回状态: ").append(response.code()).append("\r\n");
        }
    }

    /**
     * 发送消息，文本
     * @param receiveId
     * @param contentStr
     * @param security
     * @throws IOException
     */
    public static void sendText(String receiveId, String contentStr, String security) throws IOException {
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
        try (Response response = client.newCall(request).execute();) {
            log.info("消息发送状态:{}, receive_id:{}", response.code(), receiveId);
        }
    }

    /**
     * 通过人员ID 发送消息 到指定群
     *
     * @param chatId
     * @param content
     * @param security
     * @throws IOException
     */
    public static void sendGroupByChats(String chatId, String content, String security) throws IOException {
        JSONObject paramsJson = new JSONObject();
        paramsJson.put("receive_id", chatId);
        paramsJson.put("msg_type", "post");
        paramsJson.put("content", content);
        paramsJson.put("uuid", UUID.randomUUID());
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(paramsJson.toJSONString(), mediaType);
        Request request = new Request.Builder().url(SEND_GROUP_URL).method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", security)
                .build();
        try (Response response = client.newCall(request).execute()) {
            log.info("消息发送状态:{}, receive_id:{}", response.code(), chatId);
        }
    }

    /**
     * 获取授权密钥
     * @param appId
     * @param security
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
        return JSON.parseArray(data.getJSONArray("items").toJSONString(), GroupInfo.class);
    }

    /**
     * 通过chat_id 获取人员open_id
     * @param chats
     * @param security
     * @param userGroupList
     * @return
     */
    public static List<UserMemgerInfo> getMembersByChats(List<GroupInfo> chats, String security, List<UserGroup> userGroupList) {
        List<UserMemgerInfo> lists = new ArrayList<>();
        chats.forEach(chat -> {
            String chatId = chat.getChatId();
            String result = HttpUtil.createGet(String.format(MEMBERS_URL, chatId)).addHeaders(Map.of("Authorization", security)).execute().body();
            JSONObject jsonObject = JSON.parseObject(result, JSONObject.class);
            if (jsonObject.getInteger("code") != 0) {
                String msg = jsonObject.getString("msg");
                log.error("获取人员失败,{}", msg);
                return;
            }
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray itemsJson = data.getJSONArray("items");
            List<UserMemgerInfo> items = JSON.parseArray(itemsJson.toJSONString(), UserMemgerInfo.class);
            lists.addAll(items);
            items.forEach(e -> {
                e.setUserName(new StringBuilder(e.getName()).insert(1, " ").toString());
                addUserGroupList(chatId, e, userGroupList);
            });
            Integer memberTotal = data.getInteger("member_total");
            int itemsTotal = itemsJson.size();
            if (memberTotal > itemsTotal) {
                String pageToken = data.getString("page_token");
                query(lists, chatId, security, userGroupList, pageToken);
            }
        });
        return lists.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 分页查询
     * @param lists
     * @param chatId
     * @param security
     * @param userGroupList
     * @param pageToken
     */
    private static void query(List<UserMemgerInfo> lists, String chatId, String security, List<UserGroup> userGroupList, String pageToken) {
        String body = HttpUtil.createGet(String.format(MEMBERS_PAGE_URL, chatId, pageToken)).addHeaders(Map.of("Authorization", security)).execute().body();
        JSONObject jsonObject = JSON.parseObject(body, JSONObject.class);
        if (jsonObject.getInteger("code") != 0) {
            String msg = jsonObject.getString("msg");
            log.error("获取人员失败,{}", msg);
            return;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray itemsJson = data.getJSONArray("items");
        Integer memberTotal = data.getInteger("member_total");;
        if (memberTotal >= (itemsJson.size() + lists.size())) {
            List<UserMemgerInfo> items = JSON.parseArray(itemsJson.toJSONString(), UserMemgerInfo.class);
            items.forEach(e -> {
                e.setUserName(new StringBuilder(e.getName()).insert(1, " ").toString());
                addUserGroupList(chatId, e, userGroupList);
            });
            lists.addAll(items);
            if (lists.size() == memberTotal) {
                return;
            }
            pageToken = data.getString("page_token");
            query(lists, chatId, security, userGroupList, pageToken);
        }
    }

    private static void addUserGroupList(String chatId, UserMemgerInfo e, List<UserGroup> userGroupList) {
        UserGroup ug = new UserGroup();
        ug.setChatId(chatId);
        ug.setMemberId(e.getMemberId());
        userGroupList.add(ug);
    }

}
