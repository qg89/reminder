package com.q.reminder.reminder.task.me;

import com.lark.oapi.service.im.v1.model.DeleteMessageResp;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.ReminderMeTask
 * @Description :
 * @date :  2023.11.29 12:53
 */
@Component
@AllArgsConstructor
public class ReminderMeDeleteMessageTask implements BasicProcessor {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger omsLogger = context.getOmsLogger();
        ProcessResult processResult = new ProcessResult(true);
        Boolean hasKey = redisTemplate.hasKey(RedisKeyContents.FEISHU_MESSAGE_FEISHUMESSAGE);
        if (Boolean.FALSE.equals(hasKey)) {
            omsLogger.info("key 不存在");
            return processResult;
        }
        Object messageId = redisTemplate.opsForValue().get(RedisKeyContents.FEISHU_MESSAGE_FEISHUMESSAGE);
        if (messageId == null) {
            omsLogger.info("已被删除");
            return processResult;
        }
        DeleteMessageResp resp = BaseFeishu.groupMessage().deleteMessage((String) messageId);
        if (resp.success()) {
            redisTemplate.delete(RedisKeyContents.FEISHU_MESSAGE_FEISHUMESSAGE);
        }
        omsLogger.info("消息已撤回：{}", messageId);
        return processResult;
    }
}
