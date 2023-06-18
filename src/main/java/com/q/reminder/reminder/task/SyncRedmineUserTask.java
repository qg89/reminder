package com.q.reminder.reminder.task;

import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.HolidayUtils;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncRedmineUserTask
 * @Description : 每10天同步各项目人员信息
 * @date :  2023.01.18 16:42
 */
@Component
@RequiredArgsConstructor
public class SyncRedmineUserTask implements BasicProcessor {
    private final RedmineUserInfoService redmineUserInfoService;
    private final ProjectInfoService projectInfoService;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) {
        ProcessResult processResult = new ProcessResult(true);
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        OmsLogger log = context.getOmsLogger();
        if (HolidayUtils.isHoliday()) {
            log.info(taskName + "-放假咯");
            return processResult;
        }
        log.info(taskName + "-start");
        try {
            List<RedmineUserInfo> data = new ArrayList<>();
            for (RProjectInfo info : projectInfoService.listAll()) {
                String redmineType = info.getRedmineType();
                List<TimeEntry> timeEntries = RedmineApi.queryProjectUsers(info);
                timeEntries.stream().collect(Collectors.toMap(TimeEntry::getUserName, TimeEntry::getUserId, (v1, v2) -> v1)).forEach((userName, userId) -> {
                    RedmineUserInfo userInfo = new RedmineUserInfo();
                    userInfo.setRedmineType(redmineType);
                    userInfo.setAssigneeId(userId);
                    userInfo.setAssigneeName(userName);
                    userInfo.setUserName(userName.replace(" ", ""));
                    data.add(userInfo);
                });
            }
            redmineUserInfoService.saveOrupdateMultiIdAll(data);
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "-异常");
        }
        log.info(taskName + "-done");
        return processResult;
    }
}
