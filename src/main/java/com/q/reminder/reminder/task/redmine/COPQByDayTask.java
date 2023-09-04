package com.q.reminder.reminder.task.redmine;

import com.q.reminder.reminder.service.otherService.COPQByDayService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

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
    private final COPQByDayService copqByDayService;
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        ProcessResult processResult = new ProcessResult(true);
        OmsLogger omsLogger = context.getOmsLogger();
        copqByDayService.copqDay(omsLogger);
        return processResult;
    }
}
