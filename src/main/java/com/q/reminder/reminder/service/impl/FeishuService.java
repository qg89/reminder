package com.q.reminder.reminder.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.core.cache.LocalCache;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.constant.RedisKeyContents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.FeishuService
 * @Description :
 * @date :  2023.03.16 14:33
 */
@Service("feishuService")
public class FeishuService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private FeishuProperties feishuProperties;

    @Cacheable(cacheNames = RedisKeyContents.FEISHU_TENANT_ACCESS_TOKEN)
    public synchronized String tenantAccessToken() {
        String post = HttpUtil.post("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal", Map.of("app_id", feishuProperties.getAppId(), "app_secret", feishuProperties.getAppSecret()));
        JSONObject token = JSONObject.parseObject(post);
        String tenantAccessToken = token.getString("tenant_access_token");
        int expire = token.getIntValue("expire");
        if (expire > 30) {
            expire = (expire - 30);
        }
        redisTemplate.opsForValue().set(RedisKeyContents.FEISHU_TENANT_ACCESS_TOKEN, tenantAccessToken, expire, TimeUnit.SECONDS);
        return tenantAccessToken;
    }

    public Client client(){
        return Client.newBuilder(feishuProperties.getAppId(), feishuProperties.getAppSecret())
                .logReqAtDebug(true)
                .requestTimeout(5, TimeUnit.MINUTES)
                .tokenCache(LocalCache.getInstance())
                .openBaseUrl(BaseUrlEnum.FeiShu)
                .disableTokenCache()
                .build();
    }
}
