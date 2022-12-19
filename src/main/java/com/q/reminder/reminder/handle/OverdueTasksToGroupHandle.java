package com.q.reminder.reminder.handle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.*;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.OverdueTasksHandle
 * @Description : 过期任务提醒群组
 * @date :  2022.09.27 19:13
 */
@Log4j2
@Component
public class OverdueTasksToGroupHandle {
    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;

    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private NoneStatusService noneStatusService;
    @Autowired
    private OverdueTaskHistoryService overdueTaskHistoryService;
    @Autowired
    private UserGroupService userGroupService;

    @Scheduled(cron = "*/20 * * * * ?")
    public void sendOverdueTask() {
        String secret = FeiShuApi.getSecret(appId, appSecret);
        // 组装数据， 通过人员，获取要发送的内容
        List<ProjectInfo> projectInfoList = projectInfoService.list();
        Set<String> projectIds = projectInfoList.stream().map(ProjectInfo::getPId).collect(Collectors.toSet());

        LambdaQueryWrapper<NoneStatus> qw = new LambdaQueryWrapper<>();
        qw.select(NoneStatus::getNoneStatus);
        List<String> noneStatusList = noneStatusService.listObjs(qw, (Object::toString));

        Map<String, List<Issue>> listMap = RedmineApi.queryUserList(projectIds, noneStatusList);

        // 通过人员查看对应redmine人员关系，并返回redmine姓名和飞书member_id关系
        List<UserMemgerInfo> list = userMemberService.list();
        Map<String, String> memberIds = list.stream().collect(Collectors.toMap(UserMemgerInfo::getName, UserMemgerInfo::getMemberId));

        List<OverdueTaskHistory> historys = new ArrayList<>();
        Map<String, String> sendMap = new HashMap<>();
        listMap.forEach((k, issueList) -> {
            String name = k.replace(" ", "");
            StringBuilder content = new StringBuilder();
            content.append("<at user_id=\"").append("%s").append("\">").append(name).append("</at>").append("\r\n");
            content.append("当前任务已过期,请及时处理:").append("\r\n");
            issueList.forEach(i -> {
                Integer id = i.getId();
                OverdueTaskHistory history = new OverdueTaskHistory();
                content.append(RedmineApi.REDMINE_URL + "issues/").append(id).append("\r\n");
                content.append("任务主题:").append(i.getSubject()).append("\r\n");
                content.append("=============================").append("\r\n");
                history.setAssigneeName(name);
                history.setProjectName(i.getProjectName());
                history.setSubjectName(i.getSubject());
                history.setRedmineId(id);
                history.setLastUpdateTime(i.getUpdatedOn());
                history.setType("1");
                historys.add(history);
            });

            String memberId = memberIds.get(name);
            if (StringUtils.isBlank(memberId)) {
                return;
            }
            sendMap.put(memberId, content.toString());
        });
        LambdaQueryWrapper<UserGroup> lq = new LambdaQueryWrapper<>();
        sendMap.forEach((ou, v) -> {
            lq.eq(UserGroup::getMemberId, ou);
            List<UserGroup> userGroups = userGroupService.list(lq);
            userGroups.forEach(e -> {
                try {
                    String content = String.format(v, ou);
                    FeiShuApi.sendGroupByChats(e.getChatId(), content, secret);
                } catch (IOException ex) {
                    log.error("过期任务执行失败,{}", e);
                }
            });
        });
        boolean overdueTaskHistory = overdueTaskHistoryService.saveOrUpdateBatch(historys);
        if (!overdueTaskHistory) {
            log.error("任务保存历史记录失败!");
            return;
        }
        log.info("过期任务提醒群组,执行完成");
    }
}
