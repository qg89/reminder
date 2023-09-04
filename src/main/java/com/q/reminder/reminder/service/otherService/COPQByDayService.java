package com.q.reminder.reminder.service.otherService;

import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tech.powerjob.worker.log.OmsLogger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.otherService.COPQByDayService
 * @Description :
 * @date :  2023.09.04 14:34
 */
@Service
@AllArgsConstructor
public class COPQByDayService {
    private final ProjectInfoService projectInfoService;
    private final RedisTemplate<String, Object> redisTemplate;

    public Map<String, String> copqDay(OmsLogger omsLogger) throws RedmineException {
        Map<String, String> copq = RedmineApi.copq(projectInfoService.listAll(), omsLogger);
        redisTemplate.opsForValue().set("copq:" + DateTime.now().toString("yyyyMMdd"), JSONObject.from(copq).toJSONString(),1, TimeUnit.DAYS);
        return copq;
    }
}
