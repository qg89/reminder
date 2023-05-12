package com.q.reminder.reminder.task.redmine;

import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.util.RedmineApi;
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
        String jobParams = context.getInstanceParams();
        log.info("【redmine】同步redmine工时-start");
        List<RProjectInfo> projectList = projectInfoService.listAll();
        List<RdTimeEntry> data = new ArrayList<>();
        int index = 3;
        if (StringUtils.isNotBlank(jobParams)) {
            index = Integer.parseInt(jobParams);
        }
        log.info("【redmine】同步redmine工时-时间{}天前", index);
        String timeAgo = DateTime.now().minusDays(index).toString("yyyy-MM-dd");
        try {
            for (RProjectInfo projectInfo : projectList) {
                List<RequestParam> requestParams = List.of(
                        new RequestParam("f[]", "spent_on"),
                        new RequestParam("op[spent_on]", ">="),
                        new RequestParam("v[spent_on][]", timeAgo)

                );
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
                log.info("【redmine】同步redmine工时-项目： {}", projectInfo.getProjectShortName());
            }
            rdTimeEntryService.saveOrUpdateBatchByMultiId(data);
            log.info("【redmine】同步redmine工时-查询完成，size:{}", data.size());
        } catch (Exception e) {
            throw new FeishuException(e, "【redmine】同步redmine工时-查询工时异常");
        }
        return new ProcessResult(true);
    }
}
