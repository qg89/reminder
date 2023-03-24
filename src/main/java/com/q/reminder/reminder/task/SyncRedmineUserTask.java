package com.q.reminder.reminder.task;

import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.TimeEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncRedmineUserTask
 * @Description :
 * @date :  2023.01.18 16:42
 */
@Component
public class SyncRedmineUserTask implements BasicProcessor {
    @Autowired
    private RedmineUserInfoService redmineUserInfoService;
    @Autowired
    private ProjectInfoService projectInfoService;

@Override
public ProcessResult process(TaskContext context) throws Exception {
        List<RedmineUserInfo> data = new ArrayList<>();
        for (RProjectInfo info : projectInfoService.listAll()) {
             RedmineApi.queryUserTime(info).stream().collect(Collectors.toMap(TimeEntry::getUserId, TimeEntry::getUserName, (v1, v2) -> v1)).forEach((userId, userName) -> {
                 RedmineUserInfo userInfo = new RedmineUserInfo();
                 userInfo.setRedmineType(info.getRedmineType());
                 userInfo.setAssigneeId(userId);
                 userInfo.setUserName(userName);
                 userInfo.setAssigneeName(userName.replace(" ", ""));
                 data.add(userInfo);
             });
        }
        redmineUserInfoService.saveOrupdateMultiIdAll(data);
        return new ProcessResult(true);
    }
}
