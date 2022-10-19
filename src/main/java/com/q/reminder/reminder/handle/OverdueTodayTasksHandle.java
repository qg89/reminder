package com.q.reminder.reminder.handle;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.OverdueTasksHandle
 * @Description : 当天17点提醒
 * @date :  2022.10.18 17:02
 */
@Log4j2
@Component
public class OverdueTodayTasksHandle {

    @Autowired
    private QueryTasksToMemberBase queryTasksToMemberBase;

    @Scheduled(cron = "0 30 17 * * ?")
    public void query() {
        queryTasksToMemberBase.feiShu(0, "Resolved");
    }
}
