package com.q.reminder.reminder.task;

import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.NoneStatusService;
import com.q.reminder.reminder.task.base.QueryTasksToMemberBase;
import com.q.reminder.reminder.util.HolidayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.OverdueTasksHandle
 * @Description : 当天8点个人提醒，不发群
 * @date :  2022.10.18 17:02
 */
@Component
@RequiredArgsConstructor
public class Overdue1Tasks implements BasicProcessor {

    private final QueryTasksToMemberBase queryTasksToMemberBase;
    private final NoneStatusService noneStatusService;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        ProcessResult result = new ProcessResult(true);
        if (HolidayUtils.isHoliday()) {
            log.info("节假日放假!!!!");
            return result;
        }
        try {
            log.info(taskName + "-start");
            int expiredDay = Integer.parseInt(context.getJobParams());
            List<String> noneStatusList = noneStatusService.queryUnInStatus(0);
            queryTasksToMemberBase.feiShu(expiredDay, noneStatusList, Boolean.FALSE, log, taskName);
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "- 异常");
        }
        log.info(taskName + "-done");
        return result;
    }
}
