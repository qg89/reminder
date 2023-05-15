package com.q.reminder.reminder.task;

import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.entity.FsGroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.AdminInfoService;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.service.UserGroupService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.UpdateFeishuRedmineHandle
 * @Description : 每日更新当前群信息，人员信息，及人群关系
 * @date :  2022.09.27 20:11
 */
@Component
@RequiredArgsConstructor
public class UpdateFeishuRedmineTask implements BasicProcessor {
    private final GroupInfoService groupInfoService;
    private final UserMemberService userMemberService;
    private final UserGroupService userGroupService;
    private final AdminInfoService adminInfoService;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult processResult = new ProcessResult(true);
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        try {
            List<FsGroupInfo> groupToChats = BaseFeishu.groupMessage().getGroupToChats();
            List<AdminInfo> adminInfos = adminInfoService.list();
            log.info(taskName + "-获取机器人所在群组信息完成!");
            List<UserGroup> userGroupList = new ArrayList<>();
            List<UserMemgerInfo> membersByChats = BaseFeishu.groupMessage().getMembersInGroup(userGroupList);
            StringBuilder content = new StringBuilder();
            if (CollectionUtils.isEmpty(membersByChats)) {
                content.append("\r\n获取机器人所在群组信息为空");
            }
            log.info(taskName + "-开始数据保存!");
            if (!groupInfoService.saveOrUpdateBatch(groupToChats)) {
                content.append("\r\n更新机器人所在群组失败");
            }
            log.info(taskName + "-更新机器人所在群组完成!");
            if (!userMemberService.saveOrUpdateBatchAll(membersByChats, log)) {
                content.append("\r\n保存机器人所在群组和人员关系失败");
            }
            log.info(taskName + "-保存机器人所在群组和人员关系完成!");

            if (!userGroupService.saveOrUpdateBatchByMultiId(userGroupList)) {
                content.append("\r\n保存机器人所在群组和人员关系失败");
            }
            if (StringUtils.isNotBlank(content)) {
                MessageVo vo = new MessageVo();
                vo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
                vo.setMsgType("text");
                adminInfos.forEach(e -> {
                    try {
                        vo.setReceiveId(e.getMemberId());
                        vo.setContent(content.toString());
                        BaseFeishu.message().sendText(vo, log);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
            log.info(taskName + "-保存机器人所在群组和人员关系完成!");
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "-异常");
        }
        log.info(taskName + "-done");
        return processResult;
    }
}
