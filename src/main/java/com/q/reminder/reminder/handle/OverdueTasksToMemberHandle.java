package com.q.reminder.reminder.handle;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.*;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.FeiShuHandle
 * @Description : 过期任务提醒个人
 * @date :  2022.09.27 08:38
 */
@Log4j2
@Component
public class OverdueTasksToMemberHandle {

    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private OverdueTaskHistoryService overdueTaskHistoryService;
    @Autowired
    private AdminInfoService adminInfoService;
    @Autowired
    private NoneStatusService noneStatusService;

    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;
    @Value("{redmine-config.old_url}")
    private String redmineOldUrl;
    @Value("${redmine-config.api-access-key.saiko}")
    private String apiAccessKeySaiko;


    //    @Scheduled(cron = "*/20 * * * * ?")
    public void feiShu() {
        String secret = FeiShuApi.getSecret(appId, appSecret);
        StringBuilder contentAll = new StringBuilder();
        contentAll.append("当日执行情况如下(").append(new DateTime().toString("yyyy-MM-dd")).append("):\r\n");

        // 通过人员查看对应redmine人员关系，并返回redmine姓名和飞书member_id关系
        List<UserMemgerInfo> list = userMemberService.list();
        Map<String, String> memberIds = list.stream().collect(Collectors.toMap(UserMemgerInfo::getName, UserMemgerInfo::getMemberId));

        // 组装数据， 通过人员，获取要发送的内容
        List<ProjectInfo> projectInfoList = projectInfoService.list();
        Set<String> projectIds = projectInfoList.stream().map(ProjectInfo::getPId).collect(Collectors.toSet());

        LambdaQueryWrapper<NoneStatus> qw = new LambdaQueryWrapper<>();
        qw.select(NoneStatus::getNoneStatus);
        List<String> noneStatusList = noneStatusService.listObjs(qw, (Object::toString));
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("获取排除状态!").append("\r\n");

        Map<String, List<Issue>> listMap = RedmineApi.queryUserList(projectIds, noneStatusList, apiAccessKeySaiko, redmineOldUrl);
        if (CollectionUtils.isEmpty(listMap)) {
            contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("过期人员数量:").append(listMap.size()).append("\r\n");
            contentAll.append("执行完成!");
            sendAdmin(contentAll.toString(), secret);
            return;
        }
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("过期人员数量:").append(listMap.size()).append(" 查询redmine过期人员集合完成!").append("\r\n");
        // key: member_id, value: content
        Map<String, String> sendMap = new HashMap<>();
        List<OverdueTaskHistory> historys = new ArrayList<>();
        listMap.forEach((k, issueList) -> {
            String name = k.replace(" ", "");
            StringBuilder content = new StringBuilder();
            issueList.forEach(i -> {
                Integer id = i.getId();
                OverdueTaskHistory history = new OverdueTaskHistory();
                content.append("过期任务如下:").append("\r\n");
                content.append(redmineOldUrl).append("issues/").append(id).append("\r\n");
                content.append("任务主题:").append(i.getSubject()).append("\r\n");
                content.append("=============================").append("\r\n");
                history.setAssigneeName(name);
                history.setProjectName(i.getProjectName());
                history.setSubjectName(i.getSubject());
                history.setRedmineId(id);
                history.setLastUpdateTime(i.getUpdatedOn());
                historys.add(history);
            });
            String memberId = memberIds.get(name);
            if (StringUtils.isBlank(memberId)) {
                return;
            }
            sendMap.put(memberId, content.toString());
        });
        if (CollectionUtils.isEmpty(sendMap)) {
            contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("当日暂无过期任务!").append("\r\n");
        }
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("发送飞书任务开始!").append("\r\n");
        sendMap.forEach((k, v) -> {
            try {
                FeiShuApi.send(k, v, secret);
            } catch (IOException e) {
                log.error("", e);
            }
        });
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("发送飞书任务完成!").append("\r\n");
        overdueTaskHistoryService.saveOrUpdateBatch(historys);
        contentAll.append("当前步骤时间:").append(DateUtil.now()).append("→→").append("执行完成!").append("\r\n");
        sendAdmin(contentAll.toString(), secret);
        log.info("过期任务提醒个人,执行完成");
    }

    void sendAdmin(String content, String secret) {
        List<AdminInfo> adminInfos = adminInfoService.list();
        adminInfos.forEach(e -> {
            try {
                FeiShuApi.send(e.getMemberId(), content, secret);
            } catch (IOException ex) {
                log.error("管理员任务发送失败 {}", e);
            }
        });
    }
}
