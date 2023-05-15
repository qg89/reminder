package com.q.reminder.reminder.task;

import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.NoneStatusService;
import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.task.base.QueryTasksToMemberBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
    private final HoldayBase holdayBase;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        String taskName = context.getTaskName();
        ProcessResult result = new ProcessResult(true);
        try {
            if (holdayBase.queryHoliday()) {
                log.info("节假日放假!!!!");
                return result;
            }
            log.info(taskName + "-start");
            int expiredDay = Integer.parseInt(context.getJobParams());
            List<String> noneStatusList = noneStatusService.queryUnInStatus(0);
            queryTasksToMemberBase.feiShu(expiredDay, noneStatusList, Boolean.FALSE, log);
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "- 异常");
        }
        log.info(taskName + "-done");
        return result;
    }
}
