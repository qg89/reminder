package com.q.reminder.reminder.task.redmine;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.internal.RequestParam;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private final RdTimeEntryService rdTimeEntryService;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) {
        ProcessResult processResult = new ProcessResult(true);
        OmsLogger log = context.getOmsLogger();
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        DateTime sDate = DateUtil.beginOfMonth(new Date());
        DateTime eDate = DateUtil.date();
        String startTime = sDate.toString("yyy-MM-dd");
        String endTime = eDate.toString("yyy-MM-dd");
        long i = DateUtil.between(sDate, eDate, DateUnit.DAY);

        List<RequestParam> requestParams = List.of(
                new RequestParam("f[]", "spent_on"),
                new RequestParam("op[spent_on]", ">t-"),
                new RequestParam("v[spent_on][]", String.valueOf(i))
        );
        log.info(taskName + "-start");
        try {
            rdTimeEntryService.remove(Wrappers.<RdTimeEntry>lambdaQuery().between(RdTimeEntry::getSpentOn, startTime, endTime));
            log.info(taskName + "-数据删除完成， sDate：{}，eDate：{}", startTime, endTime);
            RProjectInfo info = new RProjectInfo();
            info.setPmKey("e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
            info.setRedmineType("2");
            List<RdTimeEntry> data = queryRedmineTimes(info, requestParams);
            rdTimeEntryService.saveOrUpdateBatchByMultiId(data);
            log.info(taskName + "-查询完成，size:{}", data.size());
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "-异常");
        }
        log.info(taskName + "-done");
        return processResult;
    }

    private List<RdTimeEntry> queryRedmineTimes(RProjectInfo info, List<RequestParam> requestParams) throws RedmineException {
        Collection<? extends TimeEntry> timeEntity = RedmineApi.listAllTimes(info, requestParams);
        List<RdTimeEntry> data = new ArrayList<>();
        timeEntity.forEach(timeEntry -> {
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
        return data;
    }
}
