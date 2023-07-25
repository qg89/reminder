package com.q.reminder.reminder.task.group;

import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.otherService.SyncGroupUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.group.SyncGroupUsersTask
 * @Description :
 * @date :  2023/6/29 10:59
 */
@Component
@RequiredArgsConstructor
public class SyncGroupUsersTask implements BasicProcessor {
    private final PowerJobClient client;
    private final SyncGroupUsersService syncGroupUsersService;

    @Override
    public ProcessResult process(TaskContext context){
        OmsLogger log = context.getOmsLogger();
        ProcessResult processResult = new ProcessResult(true);
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        try {
            syncGroupUsersService.exec();
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "- 异常");
        }
        return processResult;
    }
}
