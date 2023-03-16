package com.q.reminder.reminder;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import com.q.reminder.reminder.config.SpringContextUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author saiko
 */
@SpringBootApplication
@MapperScan("com.q.reminder.reminder.mapper")
@EnableMPP
public class ReminderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReminderApplication.class, args);
        SpringContextUtils.setApplicationContext(context);
    }

}
