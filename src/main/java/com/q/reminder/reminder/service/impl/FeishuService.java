package com.q.reminder.reminder.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.core.cache.LocalCache;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.constant.RedisKeyContents;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
@RequiredArgsConstructor
public class FeishuService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FeishuProperties feishuProperties;

    public synchronized String tenantAccessToken() {
        Object tenantToken = redisTemplate.opsForValue().get(RedisKeyContents.FEISHU_TENANT_ACCESS_TOKEN_KEY);
        if (tenantToken != null && StringUtils.isNotBlank(tenantToken.toString())) {
            return tenantToken.toString();
        }
        String post = HttpUtil.post("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal", Map.of("app_id", feishuProperties.getAppId(), "app_secret", feishuProperties.getAppSecret()));
        JSONObject token = JSONObject.parseObject(post);
        String tenantAccessToken = token.getString("tenant_access_token");
        int expire = token.getIntValue("expire");
        if (expire > 30) {
            expire = (expire - 30);
        }
        redisTemplate.opsForValue().set(RedisKeyContents.FEISHU_TENANT_ACCESS_TOKEN_KEY, tenantAccessToken, expire, TimeUnit.MINUTES);
        return tenantAccessToken;
    }

    public String weeklyReportSpaceId(){
        return feishuProperties.getWeeklyReportSpaceId();
    }
    public String weeklyReportToken(){
        return feishuProperties.getWeeklyReportToken();
    }

    public Client client(){
        return Client.newBuilder(feishuProperties.getAppId(), feishuProperties.getAppSecret())
                .logReqAtDebug(true)
                .requestTimeout(5, TimeUnit.MINUTES)
                .tokenCache(LocalCache.getInstance())
                .openBaseUrl(BaseUrlEnum.FeiShu)
                .tokenCache(LocalCache.getInstance())
                .build();
    }
}
