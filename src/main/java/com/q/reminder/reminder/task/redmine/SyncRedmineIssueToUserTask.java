package com.q.reminder.reminder.task.redmine;

import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SyncRedmineIssueToUserTask implements BasicProcessor {
    private final ProjectInfoService projectInfoService;
    private final RedmineUserInfoService redmineUserInfoService;

    @Override
    public ProcessResult process(TaskContext context) {
        String jobParams = context.getJobParams();
        OmsLogger log = context.getOmsLogger();
        log.info("[全部人员同步]-开始");
        List<RProjectInfo> projectList = projectInfoService.listAll();
        try {
            projectList.stream().collect(Collectors.groupingBy(RProjectInfo::getRedmineType)).forEach((k, list) -> {
                List<RedmineUserInfo> data = new ArrayList<>();
                for (RProjectInfo projectInfo : list) {
                    String redmineType = projectInfo.getRedmineType();
                    try {
                        RedmineApi.queryIssues(projectInfo).stream().filter(e -> e.getAssigneeId() != null).collect(Collectors.toMap(Issue::getAssigneeName, Issue::getAssigneeId, (v1, v2) -> v1)).forEach((name, id) -> {
                            RedmineUserInfo user = new RedmineUserInfo();
                            user.setRedmineType(redmineType);
                            user.setAssigneeId(id);
                            user.setAssigneeName(name);
                            user.setUserName(name.replace(" ", ""));
                            data.add(user);
                        });
                    } catch (RedmineException e) {
                        log.error("[全部人员同步]-获取Issue异常", e);
                    }
                    ArrayList<RedmineUserInfo> collect = data.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(RedmineUserInfo::getAssigneeId))), ArrayList::new));
                    redmineUserInfoService.saveOrupdateMultiIdAll(collect);
                    log.info("[全部人员同步]-完成，redmineType：{} ", redmineType);
                }
            });
        } catch (Exception e) {
            log.error("[全部人员同步]-异常", e);
            return new ProcessResult(false);
        }
        return new ProcessResult(true);
    }
}
