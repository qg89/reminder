package com.q.reminder.reminder.handle;

import com.q.reminder.reminder.handle.base.QueryTasksToMemberBase;
import com.q.reminder.reminder.service.NoneStatusService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.OverdueTasksHandle
 * @Description : 当天8点个人提醒，不发群
 * @date :  2022.10.18 17:02
 */
@Log4j2
@Component
public class Overdue1TasksHandle {

    @Autowired
    private QueryTasksToMemberBase queryTasksToMemberBase;
    @Autowired
    private NoneStatusService noneStatusService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void query() {
        List<String> noneStatusList = noneStatusService.queryUnInStatus(0);
        queryTasksToMemberBase.feiShu( 1, noneStatusList, Boolean.FALSE);
    }
}
