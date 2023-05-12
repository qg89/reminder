package com.q.reminder.reminder.task.redmine;

import cn.hutool.core.date.DateUtil;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.TimeEntry;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
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
 * @ClassName : com.q.reminder.reminder.task.redmine.SyncTimeEntryTask
 * @Description :
 * @date :  2023.01.19 11:18
 */
@Component
@RequiredArgsConstructor
public class SyncTimeEntryTask implements BasicProcessor {
    private final ProjectInfoService projectInfoService;
    private final RdTimeEntryService rdTimeEntryService;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        log.info("【redmine】同步redmine工时-start");
        List<RProjectInfo> projectList = projectInfoService.listAll();
        List<TimeEntry> timeData = new ArrayList<>();
        try {
            for (RProjectInfo projectInfo : projectList) {
                projectInfo.setStartDay(DateUtil.beginOfWeek(DateTime.now().minusWeeks(1).toDate()));
                timeData.addAll(RedmineApi.queryTimes(projectInfo));
            }
            log.info("【redmine】同步redmine工时-查询完成，size:{}", timeData.size());
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
        } catch (Exception e) {
            throw new FeishuException(e, "【redmine】同步redmine工时-查询工时异常");
        }
        return new ProcessResult(true);
    }
}
