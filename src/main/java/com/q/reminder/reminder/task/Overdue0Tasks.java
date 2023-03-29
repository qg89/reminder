package com.q.reminder.reminder.task;

import com.q.reminder.reminder.service.NoneStatusService;
import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.task.base.QueryTasksToMemberBase;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Description : 当天17点提醒，个人提醒
 * @date :  2022.10.18 17:02
 */
@Component
public class Overdue0Tasks implements BasicProcessor {

    @Autowired
    private QueryTasksToMemberBase queryTasksToMemberBase;
    @Autowired
    private NoneStatusService noneStatusService;
    @Autowired
    private HoldayBase holdayBase;


    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult result = new ProcessResult(true);
        try {

        if (holdayBase.queryHoliday()) {
            log.info("节假日放假!!!!");
            return result;
        }
        int expiredDay = Integer.parseInt(context.getJobParams());
        List<String> noneStatusList = noneStatusService.queryUnInStatus(0);
        // 组装数据， 通过人员，获取要发送的内容
        queryTasksToMemberBase.feiShu(expiredDay, noneStatusList, Boolean.FALSE, log);
        } catch (Exception e) {
            log.error("任务处理失败", e);
            result.setSuccess(false);
            result.setMsg("[当天17点提醒，个人提醒]-任务处理失败");
        }
        return result;
    }
}
