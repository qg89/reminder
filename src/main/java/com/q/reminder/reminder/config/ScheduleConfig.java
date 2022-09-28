package com.q.reminder.reminder.config;

import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.config.ScheduleConfig
 * @Description :
 * @date :  2022.09.26 19:38
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Method[] methods = BatchProperties.Job.class.getMethods();
        final AtomicInteger corePoolSize = new AtomicInteger();
        if (methods.length > 0) {
            Arrays.stream(methods).forEach(method -> {
                final Scheduled annotation = method.getAnnotation(Scheduled.class);
                if (Objects.nonNull(annotation)) {
                    corePoolSize.incrementAndGet();
                }
            });
        }
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize.get());
        taskRegistrar.setScheduler(executor);
    }
}
