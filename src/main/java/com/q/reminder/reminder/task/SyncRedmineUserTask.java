package com.q.reminder.reminder.task;

import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Log4j2
public class SyncRedmineUserTask {
    @Autowired
    private RedmineUserInfoService redmineUserInfoService;
    @Autowired
    private ProjectInfoService projectInfoService;

    @XxlJob("syncRedmineUserTask")
    public void syncRedmineUserTask() throws RedmineException {
        List<RedmineUserInfo> data = new ArrayList<>();
        for (RProjectInfo info : projectInfoService.list()) {
             RedmineApi.queryUserTime(info).stream().collect(Collectors.toMap(TimeEntry::getUserId, TimeEntry::getUserName, (v1, v2) -> v1)).forEach((userId, userName) -> {
                 RedmineUserInfo userInfo = new RedmineUserInfo();
                 userInfo.setRedmineType(info.getRedmineType());
                 userInfo.setAssigneeId(userId);
                 userInfo.setUserName(userName);
                 userInfo.setAssigneeName(userName.replace(" ", ""));
                 data.add(userInfo);
             });
        }
        redmineUserInfoService.saveOrUpdateBatchByMultiId(data);
    }
}
