package com.q.reminder.reminder.handle;

import com.q.reminder.reminder.handle.base.OverdueTasksAgainToGroupBase;
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
 * @Description : 每天9点半提醒，群提醒
 * @date :  2022.10.18 17:02
 */
@Log4j2
@Component
public class OverdueTasksAgain1ToGroupHandle {

    @Autowired
    private OverdueTasksAgainToGroupBase overdueTasksAgainToGroupBase;
    @Autowired
    private NoneStatusService noneStatusService;

    @Scheduled(cron = "0 30 9 * * ?")
    public void query() {
        List<String> noneStatusList = noneStatusService.queryUnInStatus(2);
        // 组装数据， 通过人员，获取要发送的内容
        overdueTasksAgainToGroupBase.overdueTasksAgainToGroup(1, noneStatusList, Boolean.FALSE);
        overdueTasksAgainToGroupBase.overdueTasksAgainToGroup(2, noneStatusList, Boolean.TRUE);
    }
}
