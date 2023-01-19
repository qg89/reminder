package com.q.reminder.reminder.task.redmine;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.redmine.SyncTimeEntryTask
 * @Description :
 * @date :  2023.01.19 11:18
 */
@Component
@Log4j2
public class SyncTimeEntryTask {
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private RdTimeEntryService rdTimeEntryService;

    @XxlJob("SyncTimeEntryTask")
    public void SyncTimeEntryTask() {
        List<ProjectInfo> projectList = projectInfoService.list();
        List<TimeEntry> timeData = new ArrayList<>();
        projectList.forEach(projectInfo -> {
            projectInfo.setStartDay(DateUtil.beginOfWeek(new DateTime()));
            try {
                timeData.addAll(RedmineApi.queryTimes(projectInfo));
            } catch (RedmineException e) {
                e.printStackTrace();
            }
        });
        List<RdTimeEntry> data = new ArrayList<>();
        for (TimeEntry timeEntry : timeData) {
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
        }
        rdTimeEntryService.saveOrUpdateBatchByMultiId(data);
    }
}
