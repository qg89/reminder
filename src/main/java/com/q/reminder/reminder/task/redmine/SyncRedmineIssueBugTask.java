package com.q.reminder.reminder.task.redmine;

import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdIssueBug;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdIssueBugService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        String jobParams = context.getJobParams();
        OmsLogger log = context.getOmsLogger();
        log.info("[通过redmine 同步bug issue]-开始");
        List<RProjectInfo> projectList = projectInfoService.listAll();
        List<RdIssueBug> bugIssueData = new ArrayList<>();
        int index = 3;
        if (StringUtils.isNotBlank(jobParams)) {
            index = Integer.parseInt(jobParams);
        }
        String threeDateAgo = DateTime.now().minusDays(index).toString("yyyy-MM-dd");
        try {
            for (RProjectInfo projectInfo : projectList) {
                List<RequestParam> requestParams = List.of(
                        new RequestParam("f[]", "created_on"),
                        new RequestParam("op[created_on]", ">="),
                        new RequestParam("v[created_on][]", threeDateAgo)

                );
                RedmineApi.queryIssueByBug(projectInfo, requestParams).forEach(iss -> {
                            boolean isBug = iss.getTracker().getName().toLowerCase(Locale.ROOT).contains("bug");
                            if (isBug) {
                                RdIssueBug issue = new RdIssueBug();
                                issue.setAssigneeId(iss.getAssigneeId());
                                issue.setAssigneeName(iss.getAssigneeName());
                                issue.setAuthorId(iss.getAuthorId());
                                issue.setAuthorName(iss.getAuthorName());
                                issue.setId(iss.getId());
                                issue.setClosedOn(iss.getClosedOn());
                                issue.setDescription(iss.getDescription());
                                issue.setDueDate(iss.getDueDate());
                                issue.setCreatedOn(iss.getCreatedOn());
                                issue.setUpdatedOn(iss.getUpdatedOn());
                                issue.setStartDate(iss.getStartDate());
                                Float estimatedHours = iss.getEstimatedHours();
                                if (estimatedHours != null) {
                                    issue.setEstimatedHours(estimatedHours.doubleValue());
                                }
                                issue.setDoneRatio(iss.getDoneRatio());
                                issue.setParentId(iss.getParentId());
                                issue.setPriorityId(iss.getPriorityId());
                                issue.setPriorityText(iss.getPriorityText());
                                issue.setProjectName(iss.getProjectName());
                                issue.setProjectid(iss.getProjectId());
                                issue.setStatusName(iss.getStatusName());
                                issue.setStatusId(iss.getStatusId());
                                Float spentHours = iss.getSpentHours();
                                if (spentHours != null) {
                                    issue.setSpentHours(spentHours.doubleValue());
                                }
                                issue.setSubject(iss.getSubject());
                                issue.setPrivateIssue(String.valueOf(iss.isPrivateIssue()));
                                Tracker tracker = iss.getTracker();
                                String customField = JSONObject.toJSONString(iss.getCustomFields());
                                issue.setCustomField(customField);
                                issue.setTracker(tracker.getId());
                                issue.setTrackerName(tracker.getName());
                                bugIssueData.add(issue);
                            }
                        }
                );
            }
            rdIssueBugService.saveOrUpdateBatchByMultiId(bugIssueData);
        } catch (Exception e) {
            throw new FeishuException(e, "[通过redmine 同步bug issue]-异常");
        }
        return new ProcessResult(true);
    }
}
