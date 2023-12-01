package com.q.reminder.reminder.task.me;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.q.reminder.reminder.constant.FeiShuContents;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;
import com.q.reminder.reminder.vo.RedmineNoneTimeVo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.RedmineTimeTask
 * @Description :
 * @date :  2023.11.14 13:54
 */
@AllArgsConstructor
@Component
public class RedmineTimeTask implements BasicProcessor {

    private final PowerJobClient client;
    private final RdTimeEntryService rdTimeEntryService;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult result = new ProcessResult(true);
        DateTime yesterday = DateUtil.yesterday();
        String dateTime = DateUtil.beginOfMonth(yesterday).toString("yyyy-MM-dd");
        String yestDay = yesterday.toString("yyyy-MM-dd");
        log.info("startDate：{},endDate：{}", dateTime, yestDay);
        try {
            List<RedmineNoneTimeVo> list = rdTimeEntryService.listNoneTimeUsers(dateTime, yestDay);
            Map<String, List<RedmineNoneTimeVo>> userMap = list.stream().collect(Collectors.groupingBy(RedmineNoneTimeVo::getUserName));
            sendFeishu(userMap, log);
        } catch (Exception e) {
            result.setSuccess(false);
            ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
            String taskName = resultDTO.getData().getJobName();
            throw new FeishuException(e, taskName + "- 异常");
        }
        return result;
    }

    private void sendFeishu(Map<String, List<RedmineNoneTimeVo>> map, OmsLogger log) {
        StringBuilder builder = new StringBuilder();
        map.forEach((name, list) -> {
            builder.append("name: ").append(name);
            list.forEach(e -> {
                builder.append("spendOn: ").append(e.getSpentOn()).append("，")
                        .append("hours： ").append(e.getHours()).append("；");
            });
            builder.append("\r\n\t");
        });
        if (StringUtils.isBlank(builder)) {
            log.info("全部都已填写！");
            return;
        }
        MessageVo vo = new MessageVo();
        vo.setReceiveId(FeiShuContents.ADMIN_MEMBERS);
        vo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
        vo.setContent(builder.toString());
        CreateMessageResp resp = BaseFeishu.message().sendText(vo, log);
        log.info("返回飞书发送状态，{}", resp.success());
    }
}
