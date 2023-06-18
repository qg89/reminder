package com.q.reminder.reminder.task.redmine;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdIssue;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdIssueService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
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
import java.util.Collection;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.redmine.SyncIssueTask
 * @Description :
 * @date :  2023.01.19 11:18
 */
@Component
@RequiredArgsConstructor
public class SyncIssueTask implements BasicProcessor {
    private final ProjectInfoService projectInfoService;
    private final RdIssueService rdIssueService;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger log = context.getOmsLogger();
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        String instanceParams = context.getInstanceParams();
        log.info(taskName + "-开始");
        List<RdIssue> data = new ArrayList<>();
        List<RProjectInfo> projectList = projectInfoService.listAll();

        int index = 7;
        if (StringUtils.isNotBlank(instanceParams)) {
            index = Integer.parseInt(instanceParams);
        }
        log.info(taskName + "-时间{}天前", index);
        try {

            for (RProjectInfo projectInfo : projectList) {
                projectInfo.setStartDay(DateUtil.beginOfWeek(DateTime.now().minusDays(index).toDate()));
                Collection<? extends Issue> issues = RedmineApi.queryIssues(projectInfo);
                for (Issue i : issues) {
                    RdIssue issue = new RdIssue();
                    issue.setAssigneeId(i.getAssigneeId());
                    issue.setAssigneeName(i.getAssigneeName());
                    issue.setAuthorId(i.getAuthorId());
                    issue.setAuthorName(i.getAuthorName());
                    issue.setId(i.getId());
                    issue.setClosedOn(i.getClosedOn());
                    issue.setDescription(i.getDescription());
                    issue.setDueDate(i.getDueDate());
                    issue.setCreatedOn(i.getCreatedOn());
                    issue.setUpdatedOn(i.getUpdatedOn());
                    issue.setStartDate(i.getStartDate());
                    Float estimatedHours = i.getEstimatedHours();
                    if (estimatedHours != null) {
                        issue.setEstimatedHours(estimatedHours.doubleValue());
                    }
                    issue.setDoneRatio(i.getDoneRatio());
                    issue.setParentId(i.getParentId());
                    issue.setPriorityId(i.getPriorityId());
                    issue.setPriorityText(i.getPriorityText());
                    issue.setProjectName(i.getProjectName());
                    issue.setProjectid(i.getProjectId());
                    issue.setStatusName(i.getStatusName());
                    issue.setStatusId(i.getStatusId());
                    Float spentHours = i.getSpentHours();
                    if (spentHours != null) {
                        issue.setSpentHours(spentHours.doubleValue());
                    }
                    issue.setSubject(i.getSubject());
                    issue.setPrivateIssue(String.valueOf(i.isPrivateIssue()));
                    Tracker tracker = i.getTracker();
                    String customField = JSONObject.toJSONString(i.getCustomFields());
                    issue.setCustomField(customField);
                    issue.setTracker(tracker.getId());
                    issue.setTrackerName(tracker.getName());
                    data.add(issue);
                }
                log.info(taskName + "-项目： {}", projectInfo.getProjectShortName());
            }
            rdIssueService.saveOrUpdateBatchByMultiId(data);
            log.info(taskName + "-更新完成");
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "-异常");
        }
        log.info(taskName + "-done");
        return new ProcessResult(true);
    }
}
