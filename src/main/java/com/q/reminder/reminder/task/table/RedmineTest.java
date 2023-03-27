package com.q.reminder.reminder.task.table;

import com.q.reminder.reminder.strategys.config.HandlerTypeContext;
import com.q.reminder.reminder.strategys.service.RedmineTypeStrategy;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.table.RedmineTest
 * @Description :
 * @date :  2023.03.27 10:53
 */
@Component
public class RedmineTest implements BasicProcessor {
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        String jobParams = context.getJobParams();
        RedmineTypeStrategy strategy = HandlerTypeContext.getInstance(Integer.valueOf(jobParams));

        System.out.println();
        return new ProcessResult(true);
    }
}
