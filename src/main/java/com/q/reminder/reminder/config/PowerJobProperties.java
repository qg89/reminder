package com.q.reminder.reminder.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.powerjob.client.PowerJobClient;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.config.PowerJobProperties
 * @Description :
 * @date :  2023/5/15 16:24
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "powerjob.worker")
public class PowerJobProperties {
    private String serverAddress;
    private String appName;
    private String password;

    @Bean
    public PowerJobClient init() {
        return new PowerJobClient(serverAddress, appName, password);
    }
}
