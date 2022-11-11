package com.q.reminder.reminder.handle;

import com.lark.oapi.Client;
import com.q.reminder.reminder.config.FeishuProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.FeishuClientHandle
 * @Description :
 * @date :  2022.11.11 17:41
 */
@Component
public class FeishuClient extends Client {
    @Autowired
    private FeishuProperties feishuProperties;

    private FeishuClient() {
    }

    @Bean
    public Client init() {
        return Client.newBuilder(feishuProperties.getAppId(), feishuProperties.getAppSecret()).build();
    }
}
