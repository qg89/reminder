package com.q.reminder.reminder.util;

import com.q.reminder.reminder.config.SpringContextUtils;
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
public class RedisUtils {
    private RedisTemplate<String, Object> redisTemplate = SpringContextUtils.getBean("redisTemplate", RedisTemplate.class);

    private static RedisUtils instance;

    private RedisUtils() {
    }

    public static synchronized RedisUtils getInstance() {
        if (instance == null) {
            instance = new RedisUtils();
        }
        return instance;
    }

    public Boolean invokeExceededTimes(String key, int hours, int count) {
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey != null && hasKey) {
            int currentCount = Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString());
            if (currentCount >= count) {
                return true;
            }
            redisTemplate.opsForValue().increment(key, 1);
        } else {
            redisTemplate.opsForValue().set(key, "1", hours, TimeUnit.HOURS);
        }
        return false;
    }

    public void removeKey(String key) {
        redisTemplate.delete(key);
    }
}
