package com.q.reminder.reminder.task.redmine;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.RdIssue;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdIssueService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Log4j2
public class SyncIssueTask {
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private RdIssueService rdIssueService;

    @XxlJob("syncIssueTask")
    public void syncIssueTask() {
        List<Issue> issueData = new ArrayList<>();
        List<ProjectInfo> projectList = projectInfoService.list();
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
            issue.setAssigneeid(i.getAssigneeId());
            issue.setAssigneename(i.getAssigneeName());
            issue.setAuthorid(i.getAuthorId());
            issue.setAuthorname(i.getAuthorName());
            issue.setId(i.getId());
            issue.setClosedon(i.getClosedOn());
            issue.setDescription(i.getDescription());
            issue.setDuedate(i.getDueDate());
            issue.setCreatedon(i.getCreatedOn());
            issue.setUpdatedon(i.getUpdatedOn());
            issue.setStartdate(i.getStartDate());
            Float estimatedHours = i.getEstimatedHours();
            if (estimatedHours != null) {
                issue.setEstimatedhours(estimatedHours.doubleValue());
            }
            issue.setNotes(i.getNotes());
            issue.setDoneratio(i.getDoneRatio());
            issue.setParentid(i.getParentId());
            issue.setPriorityid(i.getPriorityId());
            issue.setPrioritytext(i.getPriorityText());
            issue.setProjectname(i.getProjectName());
            issue.setProjectid(i.getProjectId());
            issue.setStatusname(i.getStatusName());
            issue.setStatusid(i.getStatusId());
            Float spentHours = i.getSpentHours();
            if (spentHours != null) {
                issue.setSpenthours(spentHours.doubleValue());
            }
            issue.setSubject(i.getSubject());
            issue.setPrivateissue(String.valueOf(i.isPrivateIssue()));
            data.add(issue);
        }
        rdIssueService.saveOrUpdateBatchByMultiId(data);
    }
}
