package com.q.reminder.reminder.task.me;

import com.q.reminder.reminder.service.ReminderUserConfService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.ReminderTask
 * @Description : 每周五10、12点各提醒一次
 * @date :  2023/8/17 10:57
 */
@Component
@AllArgsConstructor
public class ReminderTask implements BasicProcessor {


    private final ReminderUserConfService reminderUserConfService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger omsLogger = context.getOmsLogger();
        ProcessResult result = new ProcessResult(true);
        reminderUserConfService.reminder(omsLogger);
        return result;
    }
}
