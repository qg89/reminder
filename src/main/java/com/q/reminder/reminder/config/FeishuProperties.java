package com.q.reminder.reminder.config;

import com.lark.oapi.Client;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.config.FeishuProperties
 * @Description :
 * @date :  2022.11.03 14:22
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "feishu-config")
public class FeishuProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 6887391918318443784L;
    private String appId;
    private String appSecret;

    @Bean
    public Client init() {
        return Client.newBuilder(this.getAppId(), this.getAppSecret()).build();
    }
}
