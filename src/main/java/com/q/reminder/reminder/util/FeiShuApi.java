package com.q.reminder.reminder.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.vo.DefinitionVo;
import com.q.reminder.reminder.vo.FeatureListVo;
import com.q.reminder.reminder.vo.SendVo;
import com.q.reminder.reminder.vo.SheetVo;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.test.T
 * @Description :
 * @date :  2022.09.23 18:45
 */
@Log4j2
public abstract class FeiShuApi {
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
     */
    public static void sendText(String receiveId, String contentStr, String security) {
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
        } catch (IOException e) {
            log.error("消息发送异常, receive_id:{}", receiveId);
        }
    }

    /**
     * 发送开发人员
     * @param adminInfos
     * @param content
     * @param secret
     */
    public static void sendAdmin(List<AdminInfo> adminInfos, String content, String secret) {
        adminInfos.forEach(e -> {
            sendText(e.getMemberId(), content, secret);
        });
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
//                e.setUserName(new StringBuilder(e.getName()).insert(1, " ").toString());
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
//                e.setUserName(new StringBuilder(e.getName()).insert(1, " ").toString());
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

    private static String bearer = "t-g104aqgEZ4WWCKWYNJE2H6KGBGXCJO52RVDIYJ4M";

//    public static void main(String[] args) {
//        String spreadsheetToken = "shtcnKDY4BUliWySLgo0LnDDtme";
//        String sheetId = "ZBTGux";
//        String viewsId = "pH9hbVcCXA";
//        String range = "ZBTGux!A1:AJ2000,GVO53c!A1:O2";
//        String secret = getSecret("cli_a1144b112738d013", "AQHvpoTxE4pxjkIlcOwC1bEMoJMkJiTx");
//
////        String view = getView(spreadsheetToken, sheetId, viewsId);
////        createFilter(spreadsheetToken, sheetId, viewsId);
//        List<JSONObject> list = getRanges(spreadsheetToken, range, secret);
////        List<FeatureListVo> featureList = getFeatureList(valueRange);
////        System.out.println(featureList);
//        List<SheetVo> spredsheets = getSpredsheets(spreadsheetToken, secret);
//        System.out.println(spredsheets);
////        updateRange(spreadsheetToken);
//    }

    /**
     * 获取单个范围
     *
     * @param spreadsheetToken
     * @param ranges
     * @param secret
     */
    public static List<JSONObject> getRanges(String spreadsheetToken, String ranges, String secret) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + spreadsheetToken + "/values_batch_get?ranges=" + ranges + "&valueRenderOption=UnformattedValue&dateTimeRenderOption=FormattedString")
                .method("GET", null)
                .addHeader("Authorization", secret)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody result = response.body();
            JSONObject resultJson = JSONObject.parseObject(result.string());
            return resultJson.getJSONObject("data").getJSONArray("valueRanges").toJavaList(JSONObject.class, JSONReader.Feature.IgnoreNoneSerializable);
        } catch (IOException e) {
            log.error(e);
        }
        return new ArrayList<>();
    }

    /**
     * 获取featureId为空的需求列表
     *
     * @param cellList
     * @param sheetId
     * @return
     */
    public static List<FeatureListVo> getFeatureList(List<List> cellList, String sheetId) {
        if (CollectionUtils.isEmpty(cellList)) {
            log.info("飞书获取的需求列表为空!");
            return new ArrayList<>();
        }
        Map<String, String> fieldsMap = fieldsFeatureMap();
        List<FeatureListVo> list = new ArrayList<>();
        for (int i = 1; i < cellList.size(); i++) {
            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < cellList.get(i).size(); j++) {
                Object k = cellList.get(0).get(j);
                if (Objects.equals(k, null)) {
                    continue;
                }
                String key = k.toString().replace("\n", "").replace(" ", "");
                String fileKey = fieldsMap.get(key);
                if (StringUtils.isBlank(fileKey)) {
                    continue;
                }
                String value = Optional.ofNullable(cellList.get(i).get(j)).orElse("").toString();
                map.put(fileKey, value);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            FeatureListVo featureListVo = objectMapper.convertValue(map, FeatureListVo.class);
            if ("是".equals(featureListVo.getIsRedmine()) && StringUtils.isBlank(featureListVo.getFeatureId())) {
                featureListVo.setRange(sheetId + "!C" + (i + 1) + ":C" + (i + 1));
                list.add(featureListVo);
            }
        }
        return list;
    }

    /**
     * 获取定义
     *
     * @param cellList
     * @param definition
     * @return
     */
    public static DefinitionVo getDefinitionList(List<List> cellList, DefinitionVo definition) {
        if (CollectionUtils.isEmpty(cellList)) {
            log.info("飞书获取的定义为空!");
            return definition;
        }
        Map<String, String> fieldsMap = fieldsDefinitionMap();
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < cellList.size(); i++) {
            for (int j = 0; j < cellList.get(1).size(); j++) {
                Object key = cellList.get(0).get(j);
                if (key == null) {
                    continue;
                }
                String fileKey = fieldsMap.get(key.toString());
                if (StringUtils.isBlank(fileKey)) {
                    continue;
                }
                map.put(fileKey, Optional.ofNullable(cellList.get(1).get(j)).orElse("").toString());
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        DefinitionVo definitionVo = objectMapper.convertValue(map, DefinitionVo.class);
        CopyOptions copyOptions = new CopyOptions();
        copyOptions.setIgnoreNullValue(true);
        BeanUtil.copyProperties(definitionVo, definition, copyOptions);
        return definition;
    }


    /**
     * 创建赛选视图
     */
    public static String createView(String spreadsheetToken, String sheetId) {
        String range = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\"filter_view_id\":\"pH9hbVcCXA\",\"filter_view_name\":\"筛选视图 1\",\"range\":\"ZBTGux!A1:AJ20\"}", mediaType);
        Request request = new Request.Builder()
                .url("https://open.feishu.cn/open-apis/sheets/v3/spreadsheets/" + spreadsheetToken + "/sheets/" + sheetId + "/filter_views")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + bearer)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(string).getJSONObject("data").getJSONObject("filter_view");
            range = jsonObject.getString("range");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return range;
    }

    /**
     * 创建过滤器
     *
     * @param spreadsheetToken
     * @param sheetId
     * @param viewsId
     */
    public static void createFilter(String spreadsheetToken, String sheetId, String viewsId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\"compare_type\":\"contains\",\"condition_id\":\"M\",\"expected\":[\"待开发\"],\"filter_type\":\"text\"}", mediaType);
        Request request = new Request.Builder()
                .url("https://open.feishu.cn/open-apis/sheets/v3/spreadsheets/" + spreadsheetToken + "/sheets/" + sheetId + "/filter_views/" + viewsId + "/conditions")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + bearer)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取视图
     *
     * @param spreadsheetToken
     * @param sheetId
     * @param viewsId
     * @return
     */
    public static String getView(String spreadsheetToken, String sheetId, String viewsId) {
        String range = null;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://open.feishu.cn/open-apis/sheets/v3/spreadsheets/" + spreadsheetToken + "/sheets/" + sheetId + "/filter_views/" + viewsId)
                .method("GET", null)
                .addHeader("Authorization", "Bearer " + bearer)
                .build();
        try {
            Response response = client.newCall(request).execute();
            range = JSONObject.parseObject(response.body().string()).getJSONObject("data").getJSONObject("filter_view").getString("range");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return range;
    }

    /**
     * 获取电子表格sheets
     */
    public static List<SheetVo> getSpredsheets(String spreadsheetToken, String secret) {
        List<SheetVo> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://open.feishu.cn/open-apis/sheets/v3/spreadsheets/" + spreadsheetToken + "/sheets/query")
                .method("GET", null)
                .addHeader("Authorization", secret)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            JSONArray sheets = JSONObject.parseObject(result).getJSONObject("data").getJSONArray("sheets");
            sheets.forEach(e -> {
                SheetVo vo = new SheetVo();
                JSONObject jsonObject = (JSONObject) e;
                vo.setTitle(jsonObject.getString("title"));
                vo.setSheetId(jsonObject.getString("sheet_id"));
                list.add(vo);
            });
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 更新单元格数据
     *
     * @param spreadsheetToken
     * @param secret
     * @param range
     * @param value
     */
    public static void updateRange(String spreadsheetToken, String secret, String range, String value) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create("{\"valueRange\":{\"range\": \"" + range + "\",\n" +
                "    \"values\": [\n" +
                "      [\n" +
                "        \"" + value + "\"\n" +
                "      ]\n" +
                "    ]\n" +
                "    }\n" +
                "}", mediaType);
        Request request = new Request.Builder()
                .url("https://open.feishu.cn/open-apis/sheets/v2/spreadsheets/" + spreadsheetToken + "/values")
                .method("PUT", body)
                .addHeader("Authorization", secret)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            log.info("更新单元格成功, " + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 需求管理表中列头转换实体类字段
     *
     * @return
     */
    private static Map<String, String> fieldsFeatureMap() {
        Map<String, String> map = new HashMap<>();
        map.put("RFQID", "rfqId");
        map.put("登记Redmine", "isRedmine");
        map.put("需求ID", "featureId");
        map.put("模块", "module");
        map.put("里程碑", "milestone");
        map.put("RedmineID/链接", "redmineSubject");
        map.put("一级", "menuOne");
        map.put("二级", "menuTwo");
        map.put("三级", "menuThree");
        map.put("功能描述", "desc");
        map.put("优先级", "priority");
        map.put("需求类型", "featureType");
        map.put("来源", "featureSource");
        map.put("状态", "featureStatus");
        map.put("产品经理", "product");
        map.put("目标版本", "target");
        map.put("转测时间", "testTime");
        map.put("关联需求父ID", "parentFeatureId");
        return map;
    }

    private static Map<String, String> fieldsDefinitionMap() {
        Map<String, String> map = new HashMap<>();
        map.put("产品接口人", "product");
        map.put("应用端接口人", "application");
        map.put("测试接口人", "test");
        map.put("大数据接口人", "bigData");
        return map;
    }

}
