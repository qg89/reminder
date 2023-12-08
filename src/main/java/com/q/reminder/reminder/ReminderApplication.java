package com.q.reminder.reminder;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import com.q.reminder.reminder.util.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author saiko
 */
@Log4j2
@SpringBootApplication
@MapperScan("com.q.reminder.reminder.mapper")
@EnableMPP
public class ReminderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReminderApplication.class, args);
        SpringContextUtils.setApplicationContext(context);
//        if (SystemUtils.isLinux()) {
//            try {
//                System.load("/usr/java/openjdk-17/include/linux/ibfeishutoken.so");
//            } catch (Exception e) {
//                log.error("init lib failed", e);
//            }
//        }
    }
}
