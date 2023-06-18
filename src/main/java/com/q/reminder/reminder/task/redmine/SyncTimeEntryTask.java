package com.q.reminder.reminder.task.redmine;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.util.HolidayUtils;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.internal.RequestParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import java.util.Optional;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.redmine.SyncTimeEntryTask
 * @Description :
 * @date :  2023.01.19 11:18
 */
@Component
@RequiredArgsConstructor
public class SyncTimeEntryTask implements BasicProcessor {
    private final ProjectInfoService projectInfoService;
    private final RdTimeEntryService rdTimeEntryService;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) {
        String jobParams = Optional.ofNullable(context.getJobParams()).orElse("0");
        String instanceParams = context.getInstanceParams();
        ProcessResult processResult = new ProcessResult(true);
        OmsLogger log = context.getOmsLogger();
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        if (HolidayUtils.isHoliday()) {
            log.info(taskName + "-周末/节假日放假咯！！！！");
            return processResult;
        }
        int index = 8;
        if (StringUtils.isNotBlank(jobParams) && ReUtil.isMatch(Validator.NUMBERS, jobParams)) {
            index = Integer.parseInt(jobParams);
        }
        if (StringUtils.isNotBlank(instanceParams) && ReUtil.isMatch(Validator.NUMBERS, instanceParams)) {
            index = Integer.parseInt(instanceParams);
        }
        log.info(taskName + "-start");
        List<RProjectInfo> projectList = projectInfoService.listAll();
        List<RdTimeEntry> data = new ArrayList<>();
        log.info(taskName + "-时间{}天前", index);
        String timeAgo = DateTime.now().minusDays(index).toString("yyyy-MM-dd");
        String endTime = DateTime.now().toString("yyyy-MM-dd");
        List<RequestParam> requestParams = List.of(
                new RequestParam("f[]", "spent_on"),
                new RequestParam("op[spent_on]", ">t-"),
                new RequestParam("v[spent_on][]", String.valueOf(index))

        );
        try {
            rdTimeEntryService.remove(Wrappers.<RdTimeEntry>lambdaQuery().between(RdTimeEntry::getSpentOn, timeAgo, endTime));
            log.info(taskName + "-数据删除完成， sDate：{}，eDate：{}", timeAgo, endTime);
            for (RProjectInfo projectInfo : projectList) {
                String projectShortName = projectInfo.getProjectShortName();
                RedmineApi.getTimeEntity(projectInfo, requestParams).forEach(timeEntry -> {
                    RdTimeEntry time = new RdTimeEntry();
                    time.setId(timeEntry.getId());
                    time.setActivityId(timeEntry.getActivityId());
                    time.setComment(timeEntry.getComment());
                    time.setActivityName(timeEntry.getActivityName());
                    time.setHours(timeEntry.getHours());
                    time.setCreatedOn(timeEntry.getCreatedOn());
                    time.setIssueId(timeEntry.getIssueId());
                    time.setProjectId(timeEntry.getProjectId());
                    time.setProjectName(timeEntry.getProjectName());
                    time.setSpentOn(timeEntry.getSpentOn());
                    time.setUserid(timeEntry.getUserId());
                    time.setUpdatedOn(timeEntry.getUpdatedOn());
                    time.setUserName(timeEntry.getUserName());
                    data.add(time);
                });
                log.info(taskName + "-项目： {}.  Redmine 查询完成，dataSize：{}", projectShortName, data.size());
            }
            rdTimeEntryService.saveOrUpdateBatchByMultiId(data);
            log.info(taskName + "-查询完成，size:{}", data.size());
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "-异常");
        }
        log.info(taskName + "-done");
        return processResult;
    }
}
