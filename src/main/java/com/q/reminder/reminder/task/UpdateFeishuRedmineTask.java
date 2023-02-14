package com.q.reminder.reminder.task;

import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.AdminInfoService;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.service.UserGroupService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.FeishuJavaUtils;
import com.q.reminder.reminder.vo.MessageVo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
public class UpdateFeishuRedmineTask {
    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private AdminInfoService adminInfoService;
    @Autowired
    private Client client;

    @XxlJob("everyDaySyncMember")
    public void everyDaySyncMember() throws Exception {
        List<GroupInfo> groupToChats = FeishuJavaUtils.getGroupToChats(client);
        List<AdminInfo> adminInfos = adminInfoService.list();
        log.info("获取机器人所在群组信息完成!");
        List<UserGroup> userGroupList = new ArrayList<>();
        List<UserMemgerInfo> membersByChats = FeishuJavaUtils.getMembersByChats(client, groupToChats, userGroupList);
        StringBuilder content = new StringBuilder();
        if (CollectionUtils.isEmpty(membersByChats)) {
            content.append("\r\n获取机器人所在群组信息为空");
        }
        log.info("开始数据保存!");
        if (!groupInfoService.saveOrUpdateBatch(groupToChats)) {
            content.append("\r\n更新机器人所在群组失败");
        }
        log.info("更新机器人所在群组完成!");
        if (!userMemberService.saveOrUpdateBatchAll(membersByChats)) {
            content.append("\r\n保存机器人所在群组和人员关系失败");
        }
        log.info("保存机器人所在群组和人员关系完成!");

        if (!userGroupService.saveOrUpdateBatchByMultiId(userGroupList)) {
            content.append("\r\n保存机器人所在群组和人员关系失败");
        }
        if (StringUtils.isNotBlank(content)) {
            MessageVo vo = new MessageVo();
            vo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
            vo.setClient(client);
            vo.setMsgType("text");
            adminInfos.forEach(e -> {
                try {
                    vo.setReceiveId(e.getMemberId());
                    vo.setContent(content.toString());
                    FeishuJavaUtils.sendContent(vo);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        log.info("保存机器人所在群组和人员关系完成!");
        log.info("每日更新当前群信息，人员信息，及人群关系, 任务执行成功!");
    }
}
