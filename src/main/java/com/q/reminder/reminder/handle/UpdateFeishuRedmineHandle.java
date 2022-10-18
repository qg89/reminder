package com.q.reminder.reminder.handle;

import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.service.UserGroupService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.FeiShuApi;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.UpdateFeishuRedmineHandle
 * @Description : 每日更新当前群信息，人员信息，及人群关系
 * @date :  2022.09.27 20:11
 */
@Log4j2
@Component
public class UpdateFeishuRedmineHandle {
    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private UserGroupService userGroupService;

    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;

    @Scheduled(cron = "*/20 * * * * ?")
//    @Scheduled(cron = "0 0 23 ? * MON-FRI")
    public void update() {
        String secret = FeiShuApi.getSecret(appId, appSecret);
        List<GroupInfo> groupToChats = FeiShuApi.getGroupToChats(secret);
        if (groupToChats == null) {
            log.error("获取机器人所在群组信息为空!");
            return;
        }
        log.info("获取机器人所在群组信息完成!");
        List<UserGroup> userGroupList = new ArrayList<>();
        List<UserMemgerInfo> membersByChats = FeiShuApi.getMembersByChats(groupToChats, secret, userGroupList);
        if (membersByChats.isEmpty()) {
            log.error("获取机器人所在群组信息为空");
        }


        log.info("开始数据保存!");
        boolean group = groupInfoService.saveOrUpdateBatch(groupToChats);
        if (!group) {
            log.error("更新机器人所在群组失败");
        }
        log.info("更新机器人所在群组完成!");

        membersByChats.forEach(e -> e.setUserName(new StringBuilder(e.getName()).insert(1, " ").toString()));
        boolean member = userMemberService.saveOrUpdateBatch(membersByChats);
        if (!member) {
            log.error("保存机器人所在群组和人员关系失败!");
        }
        log.info("保存机器人所在群组和人员关系完成!");

        boolean userGroup = userGroupService.saveBatchAll(userGroupList);
        if (!userGroup) {
            log.error("保存机器人所在群组和人员关系失败!");
        }
        log.info("保存机器人所在群组和人员关系完成!");
        log.info("每日更新当前群信息，人员信息，及人群关系, 任务执行成功!");
    }
}
