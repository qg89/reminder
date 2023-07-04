package com.q.reminder.reminder.task.redmine;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.TimeEntry;
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

import java.util.*;

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
        int index = 8;
        if (StringUtils.isNotBlank(jobParams) && NumberUtil.isNumber(jobParams)) {
            index = Integer.parseInt(jobParams);
        }
        String instanceParamsStr = JSONObject.parse(instanceParams).getString("instanceParams");
        if (StringUtils.isNotBlank(instanceParamsStr) && NumberUtil.isNumber(instanceParamsStr)) {
            index = Integer.parseInt(instanceParamsStr);
        }
        DateTime now = DateTime.now();
        String startTime;
        String endTime;
        List<RequestParam> requestParams;
        // index = -1 ，上个月1~30
        if (index < 0) {
            Date date = now.plusMonths(index).toDate();
            startTime = DateUtil.beginOfMonth(date).toString("yyyy-MM-dd");
            endTime = DateUtil.endOfMonth(date).toString("yyyy-MM-dd");
            requestParams = List.of(
                    new RequestParam("f[]", "spent_on"),
                    new RequestParam("op[spent_on]", "lm")
            );
        } else {
            startTime = now.minusDays(index).toString("yyyy-MM-dd");
            endTime = now.toString("yyyy-MM-dd");
            requestParams = List.of(
                    new RequestParam("f[]", "spent_on"),
                    new RequestParam("op[spent_on]", ">t-"),
                    new RequestParam("v[spent_on][]", String.valueOf(index))
            );
        }
        log.info(taskName + "-start");
        List<RProjectInfo> projectList = projectInfoService.listAll();
        List<RdTimeEntry> data = new ArrayList<>();
        log.info(taskName + "-开始时间 {}， 结束时间，{}", startTime, endTime);
        try {
            rdTimeEntryService.remove(Wrappers.<RdTimeEntry>lambdaQuery().between(RdTimeEntry::getSpentOn, startTime, endTime));
            log.info(taskName + "-数据删除完成， sDate：{}，eDate：{}", startTime, endTime);
            for (RProjectInfo projectInfo : projectList) {
                String projectShortName = projectInfo.getProjectShortName();
                Collection<? extends TimeEntry> timeEntity = RedmineApi.getTimeEntity(projectInfo, requestParams);
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
