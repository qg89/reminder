package com.q.reminder.reminder.task;

import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.task.base.QueryTasksToMemberBase;
import com.q.reminder.reminder.service.NoneStatusService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.OverdueTasksHandle
 * @Description : 当天17点提醒，个人提醒
 * @date :  2022.10.18 17:02
 */
@Log4j2
@Component
public class Overdue0Tasks {

    @Autowired
    private QueryTasksToMemberBase queryTasksToMemberBase;
    @Autowired
    private NoneStatusService noneStatusService;
    @Autowired
    private HoldayBase holdayBase;

    @XxlJob("overdue0TasksHandle")
    public void query() {
        if (holdayBase.queryHoliday()) {
            log.info("节假日放假!!!!");
            return;
        }
        int expiredDay = Integer.parseInt(XxlJobHelper.getJobParam());
        List<String> noneStatusList = noneStatusService.queryUnInStatus(0);
        // 组装数据， 通过人员，获取要发送的内容
        queryTasksToMemberBase.feiShu(expiredDay, noneStatusList, Boolean.FALSE);
    }
}
