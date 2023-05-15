package com.q.reminder.reminder.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.NoneStatus;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.NoneStatusService;
import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.task.base.OverdueTasksAgainToGroupBase;
import com.q.reminder.reminder.vo.QueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.OverdueTasksHandle
 * @Description : 每天9点半提醒，群提醒
 * @date :  2022.10.18 17:02
 */
@Component
@RequiredArgsConstructor
public class OverdueTasksAgainToGroupTask implements BasicProcessor {

    private final OverdueTasksAgainToGroupBase overdueTasksAgainToGroupBase;
    private final NoneStatusService noneStatusService;
    private final HoldayBase holdayBase;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult result = new ProcessResult(true);
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        try {
            if (holdayBase.queryHoliday()) {
                log.info("节假日放假!!!!");
                return result;
            }

            LambdaQueryWrapper<NoneStatus> lq = new LambdaQueryWrapper<>();
            lq.in(NoneStatus::getExpiredDays, 1, 2);
            List<NoneStatus> noneStatusList = noneStatusService.list(lq);
            Map<String, List<NoneStatus>> statusMap = noneStatusList.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getExpiredDays())));

            QueryVo vo = new QueryVo();
            vo.setExpiredDay(1);
            vo.setContainsStatus(Boolean.FALSE);
            if (statusMap.containsKey("1")) {
                vo.setNoneStatusList(statusMap.get("1").stream().map(NoneStatus::getNoneStatus).collect(Collectors.toList()));
            }
            if (!CollectionUtils.isEmpty(noneStatusList)) {
                // 组装数据， 通过人员，获取要发送的内容
                overdueTasksAgainToGroupBase.overdueTasksAgainToGroup(vo, log, taskName);
                log.info(taskName +" - 非[Resolved]执行成功");
            }
            if (statusMap.containsKey("2")) {
                vo.setNoneStatusList(statusMap.get("2").stream().map(NoneStatus::getNoneStatus).collect(Collectors.toList()));
            }
            vo.setExpiredDay(2);
            vo.setContainsStatus(Boolean.TRUE);
            vo.setRedminderType("(Resolved)");
            overdueTasksAgainToGroupBase.overdueTasksAgainToGroup(vo, log, taskName);
            log.info(taskName + " - 执行完成");
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "-异常");
        }
        return result;
    }
}
