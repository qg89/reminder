package com.q.reminder.reminder.task;

import com.lark.oapi.Client;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncTableRecordSampleTask
 * @Description :
 * @date :  2023.01.17 11:40
 */
@Log4j2
@Component
public class SyncTableRecordsTask {
    @Autowired
    private Client client;

    @XxlJob("syncTableRecordTask")
    public void syncTableRecordTask() {

    }
}
