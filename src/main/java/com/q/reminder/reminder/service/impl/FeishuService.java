package com.q.reminder.reminder.service.impl;

import com.lark.oapi.Client;
import com.lark.oapi.core.cache.LocalCache;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.q.reminder.reminder.config.FeishuProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private final FeishuProperties feishuProperties;


    public String weeklyReportSpaceId(){
        return feishuProperties.getWeeklyReportSpaceId();
    }
    public String weeklyReportToken(){
        return feishuProperties.getWeeklyReportToken();
    }

    public Client client(){
        return Client.newBuilder(feishuProperties.getAppId(), feishuProperties.getAppSecret())
                .requestTimeout(5, TimeUnit.MINUTES)
                .openBaseUrl(BaseUrlEnum.FeiShu)
                .tokenCache(LocalCache.getInstance())
                .build();
    }
}
