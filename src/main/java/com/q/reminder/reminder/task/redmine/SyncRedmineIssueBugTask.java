package com.q.reminder.reminder.task.redmine;

import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdIssueBug;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdIssueBugService;
import com.q.reminder.reminder.strategys.config.HandlerTypeContext;
import com.q.reminder.reminder.strategys.service.RedmineTypeStrategy;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.redmine.SyncRedmineIssueToUserTask
 * @Description : 通过redmine 同步bug issue
 * @date :  2023.04.07 10:18
 */
@Component
@RequiredArgsConstructor
public class SyncRedmineIssueBugTask implements BasicProcessor {
    private final ProjectInfoService projectInfoService;
    private final RdIssueBugService rdIssueBugService;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        log.info("[通过redmine 同步bug issue]-开始");
        List<RProjectInfo> projectList = projectInfoService.listAll();
        List<RdIssueBug> bugIssueData = new ArrayList<>();
        List<Issue> issueData = new ArrayList<>();
        try {
            for (RProjectInfo projectInfo : projectList) {
                RedmineTypeStrategy redmineTypeStrategy = HandlerTypeContext.getInstance(Integer.parseInt(projectInfo.getRedmineType()));
                List<Issue> list = RedmineApi.queryIssueByBug(projectInfo, redmineTypeStrategy.getBugParams());
                issueData.addAll(list);
            }
            for (Issue i : issueData) {
                RdIssueBug issue = new RdIssueBug();
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
                bugIssueData.add(issue);
            }
            rdIssueBugService.saveOrUpdateBatchByMultiId(bugIssueData);
        } catch (Exception e) {
            log.error("[通过redmine 同步bug issue]-异常", e);
            return new ProcessResult(false);
        }
        return new ProcessResult(true);
    }
}
