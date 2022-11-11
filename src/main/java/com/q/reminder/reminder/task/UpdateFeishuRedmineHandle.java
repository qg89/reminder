package com.q.reminder.reminder.task;

import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.AdminInfoService;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.service.UserGroupService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.xxl.job.core.handler.annotation.XxlJob;
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
    @Autowired
    private AdminInfoService adminInfoService;
    @Autowired
    private FeishuProperties feishuProperties;

    @XxlJob("everyDaySyncMember")
    public void update() {
        String secret = FeiShuApi.getSecret(feishuProperties.getAppId(), feishuProperties.getAppSecret());
        List<GroupInfo> groupToChats = FeiShuApi.getGroupToChats(secret);
        List<AdminInfo> adminInfos = adminInfoService.list();
        if (groupToChats == null) {
            FeiShuApi.sendAdmin(adminInfos, "获取机器人所在群组信息为空!", secret);
            return;
        }
        log.info("获取机器人所在群组信息完成!");
        List<UserGroup> userGroupList = new ArrayList<>();
        List<UserMemgerInfo> membersByChats = FeiShuApi.getMembersByChats(groupToChats, secret, userGroupList);
        if (membersByChats.isEmpty()) {
            FeiShuApi.sendAdmin(adminInfos, "获取机器人所在群组信息为空", secret);
        }

        log.info("开始数据保存!");
        boolean group = groupInfoService.saveOrUpdateBatch(groupToChats);
        if (!group) {
            FeiShuApi.sendAdmin(adminInfos, "更新机器人所在群组失败", secret);
        }
        log.info("更新机器人所在群组完成!");

        Boolean member = userMemberService.saveOrUpdateBatchAll(membersByChats);
        if (!member) {
            FeiShuApi.sendAdmin(adminInfos, "保存机器人所在群组和人员关系失败", secret);
        }
        log.info("保存机器人所在群组和人员关系完成!");

        boolean userGroup = userGroupService.saveOrUpdateBatchByMultiId(userGroupList);
        if (!userGroup) {
            FeiShuApi.sendAdmin(adminInfos, "保存机器人所在群组和人员关系失败", secret);
        }
        log.info("保存机器人所在群组和人员关系完成!");
        log.info("每日更新当前群信息，人员信息，及人群关系, 任务执行成功!");
    }
}
