package com.q.reminder.reminder.handle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.NoneStatus;
import com.q.reminder.reminder.handle.base.OverdueTasksAgainToGroupBase;
import com.q.reminder.reminder.service.NoneStatusService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
//    @Scheduled(cron = "0/20 * * * * ?")
    public void query() {
        LambdaQueryWrapper<NoneStatus> lq = new LambdaQueryWrapper<>();
        lq.in(NoneStatus::getExpiredDays, 1, 2);
        List<NoneStatus> noneStatusList = noneStatusService.list(lq);
        Map<String, List<NoneStatus>> statusMap = noneStatusList.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getExpiredDays())));
        List<String> noneStatusStrList = new ArrayList<>();
        if (statusMap.containsKey("1")) {
           noneStatusStrList = statusMap.get("1").stream().map(NoneStatus::getNoneStatus).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(noneStatusList)) {
            // 组装数据， 通过人员，获取要发送的内容
            overdueTasksAgainToGroupBase.overdueTasksAgainToGroup(1, noneStatusStrList, Boolean.FALSE);
        }
        if (statusMap.containsKey("2")) {
            noneStatusStrList = statusMap.get("2").stream().map(NoneStatus::getNoneStatus).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(noneStatusList)) {
            overdueTasksAgainToGroupBase.overdueTasksAgainToGroup(2, noneStatusStrList, Boolean.TRUE);
        }
    }
}
