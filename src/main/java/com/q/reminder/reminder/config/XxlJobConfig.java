//package com.q.reminder.reminder.config;
//
//import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author : saiko
// * @version : v1.0
// * @ClassName : com.q.reminder.reminder.config.XxlJobConfig
// * @Description :
// * @date :  2022.11.01 14:53
// */
//@Configuration
//@Log4j2
//public class XxlJobConfig {
//
//    @Value("${xxl.job.admin.addresses}")
//    private String adminAddresses;
//
//    @Value("${xxl.job.executor.appname}")
//    private String appName;
//
//    @Value("${xxl.job.executor.ip}")
//    private String ip;
//
//    @Value("${xxl.job.executor.port}")
//    private int port;
//
//    @Value("${xxl.job.accessToken}")
//    private String accessToken;
//
//    @Value("${xxl.job.executor.logpath}")
//    private String logPath;
//
//    @Value("${xxl.job.executor.logretentiondays}")
//    private int logRetentionDays;
//
//    @Bean
//    public XxlJobSpringExecutor xxlJobExecutor() {
//        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
//        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
//        xxlJobSpringExecutor.setAppname(appName);
//        xxlJobSpringExecutor.setIp(ip);
//        xxlJobSpringExecutor.setPort(port);
//        xxlJobSpringExecutor.setAccessToken(accessToken);
//        xxlJobSpringExecutor.setLogPath(logPath);
//        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
//
//        return xxlJobSpringExecutor;
//    }
//}
