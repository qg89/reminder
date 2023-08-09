package com.q.reminder.reminder.task.redmine;

import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.util.RedmineApi;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.redmine.COPQByDayTask
 * @Description :
 * @date :  2023/7/7 13:51
 */
@Component
@AllArgsConstructor
public class COPQByDayTask implements BasicProcessor {
    private final ProjectInfoService projectInfoService;
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        ProcessResult processResult = new ProcessResult(true);
        Map<String, String> copq = RedmineApi.copq(projectInfoService.listAll(), context.getOmsLogger());
//        redisTemplate.opsForHash().putAll(RedisKeyContents.COPQ_DAY, copq);
        redisTemplate.opsForValue().set(RedisKeyContents.COPQ_DAY, JSONObject.from(copq).toJSONString(),1, TimeUnit.DAYS);
        return processResult;
    }
}
