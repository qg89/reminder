package com.q.reminder.reminder.util.feishu;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.util.feishu.cloud.documents.Documents;
import com.q.reminder.reminder.util.feishu.cloud.space.Space;
import com.q.reminder.reminder.util.feishu.cloud.table.Table;
import com.q.reminder.reminder.util.feishu.group.GroupMessage;
import com.q.reminder.reminder.util.feishu.message.Message;
import com.q.reminder.reminder.util.feishu.wiki.Wiki;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.base.FeishuUtils
 * @Description :
 * @date :  2023.02.23 11:32
 */
@Component
public class BaseFeishu {
    protected BaseFeishu() {
    }

    protected Client CLIENT = null;
    protected String TENANT_ACCESS_TOKEN;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private FeishuProperties feishuProperties;
    @Autowired
    private Client client;

    @Cacheable(cacheNames = "TENANT_ACCESS_TOKEN")
    public String tenantAccessToken() {
        String post = HttpUtil.post("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal", Map.of("app_id", feishuProperties.getAppId(), "app_secret", feishuProperties.getAppSecret()));
        JSONObject token = JSONObject.parseObject(post);
        String tenantAccessToken = token.getString("tenant_access_token");
        redisTemplate.opsForValue().set("TENANT_ACCESS_TOKEN", tenantAccessToken, token.getInteger("expire") - 10, TimeUnit.SECONDS);
        return tenantAccessToken;
    }

    /**
     * 云文档-云空间
     *
     * @return
     */
    public Space cloud() {
        init();
        return Space.getInstance();
    }

    /**
     * 云文档-文档
     *
     * @return
     */
    public Documents block() {
        init();
        return Documents.getInstance();
    }

    /**
     * 消息
     *
     * @return
     */
    public Message message() {
        init();
        return Message.getInstance();
    }

    /**
     * 知识空间
     *
     * @return
     */
    public Wiki wiki() {
        init();
        return Wiki.getInstance();
    }

    /**
     * 群组
     *
     * @return
     */
    public GroupMessage groupMessage() {
        init();
        return GroupMessage.getInstance();
    }

    /**
     * 云文档-多维表格
     *
     * @return
     */
    public Table table() {
        init();
        return Table.getInstance();
    }

    private void init() {
        CLIENT = client;
        TENANT_ACCESS_TOKEN = tenantAccessToken();
    }
}
