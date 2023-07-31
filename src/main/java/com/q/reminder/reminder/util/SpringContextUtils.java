package com.q.reminder.reminder.util;

import lombok.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.config.SpringContextUtils
 * @Description :
 * @date :  2023.03.16 13:16
 */
public final class SpringContextUtils {
    public static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return SpringContextUtils.applicationContext;
    }

    public static void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeanOfType(@Nullable Class<T> type) {
        return applicationContext.getBeansOfType(type);
    }
}
