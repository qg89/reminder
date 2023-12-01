package com.q.reminder.reminder.task.me;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.q.reminder.reminder.constant.FeiShuContents;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.Map;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.ReminderMeTask
 * @Description :
 * @date :  2023.11.29 12:53
 */
@Component
@AllArgsConstructor
public class ReminderMeTask  implements BasicProcessor {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        String jobParams = context.getJobParams();
        OmsLogger omsLogger = context.getOmsLogger();
        String todayNum = String.valueOf(DateUtil.thisDayOfWeek());
        String content = null;
        if (JSONUtil.isTypeJSON(jobParams)) {
            Map<String, String> map = JSONObject.parseObject(jobParams, Map.class);
            content = map.get(todayNum);
        }
        MessageVo vo = new MessageVo();
        vo.setReceiveId(FeiShuContents.ADMIN_MEMBERS);
        vo.setContent(content);
        vo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
        CreateMessageResp resp = BaseFeishu.message().sendText(vo, omsLogger);
        String messageId = resp.getData().getMessageId();
        redisTemplate.opsForValue().set(RedisKeyContents.FEISHU_MESSAGE_FEISHUMESSAGE, messageId);
        return new ProcessResult(true);
    }
}
