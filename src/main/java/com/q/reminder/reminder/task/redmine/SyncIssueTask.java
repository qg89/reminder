package com.q.reminder.reminder.task.redmine;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdIssue;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdIssueService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.redmine.SyncIssueTask
 * @Description :
 * @date :  2023.01.19 11:18
 */
@Component
public class SyncIssueTask implements BasicProcessor {
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private RdIssueService rdIssueService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        List<Issue> issueData = new ArrayList<>();
        List<RProjectInfo> projectList = projectInfoService.listAll();
        projectList.forEach(projectInfo -> {
            projectInfo.setStartDay(DateUtil.beginOfWeek(DateTime.now().minusWeeks(1).toDate()));
            try {
                issueData.addAll(RedmineApi.queryIssues(projectInfo));
            } catch (RedmineException e) {
                e.printStackTrace();
            }
        });
        List<RdIssue> data = new ArrayList<>();
        for (Issue i : issueData) {
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
        rdIssueService.saveOrUpdateBatchByMultiId(data);
        return new ProcessResult(true, "success");
    }
}
