package com.q.reminder.reminder.config;

import org.springframework.context.ApplicationContext;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.config.SpringContextUtils
 * @Description :
 * @date :  2023.03.16 13:16
 */
public class SpringContextUtils {
    public static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return SpringContextUtils.applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return getApplicationContext().getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
