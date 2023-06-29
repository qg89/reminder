package com.q.reminder.reminder.service.taskService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.service.im.v1.model.ListMember;
import com.q.reminder.reminder.constant.GroupInfoType;
import com.q.reminder.reminder.entity.FsGroupInfo;
import com.q.reminder.reminder.entity.FsUserMemberInfoTmp;
import com.q.reminder.reminder.service.FsUserMemberInfoTmpService;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.taskService.SyncGroupUsersService
 * @Description :
 * @date :  2023/6/29 11:08
 */
@Service
@AllArgsConstructor
public class SyncGroupUsersService {
    private final GroupInfoService groupInfoService;
    private final FsUserMemberInfoTmpService userMemberInfoTmpService;
    public void exec() throws Exception{
        userMemberInfoTmpService.remove(Wrappers.lambdaUpdate());
        LambdaQueryWrapper<FsGroupInfo> eq = Wrappers.<FsGroupInfo>lambdaQuery().eq(FsGroupInfo::getSendType, GroupInfoType.DEP_GROUP);
        Optional<FsGroupInfo> first = Optional.ofNullable(groupInfoService.list(eq)).orElse(new ArrayList<>()).stream().findFirst();
        if (first.isEmpty()) {
            return;
        }
        FsGroupInfo fsGroupInfo = first.get();
        List<ListMember> listMember = BaseFeishu.groupMessage().listUsersToChats(fsGroupInfo.getChatId());
        List<FsUserMemberInfoTmp> data = new ArrayList<>();
        listMember.forEach(e -> {
            FsUserMemberInfoTmp tmp = new FsUserMemberInfoTmp();
            tmp.setName(e.getName());
            tmp.setMemberId(e.getMemberId());
            tmp.setTenantKey(e.getTenantKey());
            tmp.setMemberIdType(e.getMemberIdType());
            data.add(tmp);
        });
        userMemberInfoTmpService.saveBatch(data);
    }
}
