package com.q.reminder.reminder.task.redmine;

import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.redmine.SyncRedmineIssueToUserTask
 * @Description : 通过redmine 同步人员
 * @date :  2023.04.07 10:18
 */
@Component
public class SyncRedmineIssueToUserTask implements BasicProcessor {
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private RedmineUserInfoService redmineUserInfoService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        String jobParams = context.getJobParams();
        OmsLogger log = context.getOmsLogger();
        log.info("[全部人员同步]-开始");
        List<RProjectInfo> projectList = projectInfoService.listAll();
        List<RedmineUserInfo> data = new ArrayList<>();
        for (RProjectInfo projectInfo : projectList) {
            List<? extends Issue> li = RedmineApi.queryIssues(projectInfo).stream().filter(e -> e.getAssigneeId() != null).toList();
            for (Issue issue : li) {
                String name = issue.getAssigneeName();
                RedmineUserInfo user = new RedmineUserInfo();
                user.setRedmineType(projectInfo.getRedmineType());
                user.setAssigneeId(issue.getAssigneeId());
                user.setAssigneeName(name);
                user.setUserName(name.replace(" ", ""));
                data.add(user);
            }
        }
        ArrayList<RedmineUserInfo> collect = data.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RedmineUserInfo::getAssigneeId))), ArrayList::new));
        redmineUserInfoService.saveOrupdateMultiIdAll(collect);
        log.info("[全部人员同步]-完成");
        return new ProcessResult(true);
    }
}
