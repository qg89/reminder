package com.q.reminder.reminder.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.RedisUtils
 * @Description :
 * @date :  23/04/2023 10:07
 */
public final class RedisUtils {
    private final RedisTemplate<String, Integer> redisTemplate = SpringContextUtils.getBean("redisTemplate", RedisTemplate.class);
    private static RedisUtils instance;

    private RedisUtils() {
    }

    public static synchronized RedisUtils getInstance() {
        if (instance == null) {
            instance = new RedisUtils();
        }
        return instance;
    }

    public Boolean invokeExceededTimes(@NotNull String key, int times, int count) {
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey != null && hasKey) {
            int currentCount = Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString());
            if (currentCount >= count) {
                return false;
            }
            redisTemplate.opsForValue().increment(key, 1);
        } else {
            redisTemplate.opsForValue().set(key, 1, times, TimeUnit.MINUTES);
        }
        return true;
    }

    public void removeKey(@NotNull String key) {
        if (!Objects.equals(null, redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }
    }
}
