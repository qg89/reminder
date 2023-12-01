package com.q.reminder.reminder.util.feishu.group;

import cn.hutool.core.bean.BeanUtil;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.im.v1.enums.GetChatMembersMemberIdTypeEnum;
import com.lark.oapi.service.im.v1.enums.ListChatUserIdTypeEnum;
import com.lark.oapi.service.im.v1.model.*;
import com.q.reminder.reminder.entity.FsGroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.util.feishu.BaseFeishu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.group.GroupMessage
 * @Description :
 * @date :  2023.02.23 12:03
 */
public class GroupMessage extends BaseFeishu {

    private static GroupMessage groupMessage;

    private GroupMessage() {
        super();
    }

    public static synchronized GroupMessage getInstance() {
        if (groupMessage == null) {
            groupMessage = new GroupMessage();
        }
        return groupMessage;
    }


    /**
     * 获取机器人所在群组
     *
     * @return
     * @throws Exception
     */
    public List<FsGroupInfo> getGroupToChats() {
        ListChatReq req = ListChatReq.newBuilder().userIdType(ListChatUserIdTypeEnum.OPEN_ID).build();
        ListChatResp resp;
        try {
            resp = CLIENT.im().chat().list(req);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 获取机器人所在群组异常");
        }
        List<FsGroupInfo> list = new ArrayList<>();
        if (resp.success()) {
            ListChatRespBody data = resp.getData();
            ListChat[] items = data.getItems();
            ArrayList<ListChat> listChats = new ArrayList<>(Arrays.asList(items));
            listChats.forEach(e -> {
                FsGroupInfo fsGroupInfo = new FsGroupInfo();
                BeanUtil.copyProperties(e, fsGroupInfo);
                list.add(fsGroupInfo);
            });
        }
        return list;
    }

    public List<UserMemgerInfo> getMembersInGroup(List<UserGroup> userGroupList) {
        String chatId = "oc_68be4dc6c1143059ebeaa5d6699773a2";
        List<UserMemgerInfo> items = new ArrayList<>();
        GetChatMembersReq req = GetChatMembersReq.newBuilder()
                .chatId(chatId)
                .memberIdType(GetChatMembersMemberIdTypeEnum.OPEN_ID)
                .pageSize(20)
                .build();
        GetChatMembersResp resp;
        try {
            resp = CLIENT.im().chatMembers().get(req);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 通过机器人获取人员异常");
        }
        GetChatMembersRespBody data = resp.getData();
        for (ListMember dataItem : data.getItems()) {
            UserMemgerInfo userMemgerInfo = new UserMemgerInfo();
            BeanUtil.copyProperties(dataItem, userMemgerInfo);
            items.add(userMemgerInfo);
            addUserGroupList(chatId, userMemgerInfo, userGroupList);
        }
        String pageToken = data.getPageToken();
        if (data.getHasMore()) {
            query(items, chatId, userGroupList, pageToken, req);
        }
        return items.stream().distinct().toList();
    }

    private void addUserGroupList(String chatId, UserMemgerInfo e, List<UserGroup> userGroupList) {
        UserGroup ug = new UserGroup();
        ug.setChatId(chatId);
        ug.setMemberId(e.getMemberId());
        userGroupList.add(ug);
    }

    /**
     * 分页查询
     *
     * @param lists
     * @param chatId
     * @param userGroupList
     * @param pageToken
     * @param req
     */
    private void query(List<UserMemgerInfo> lists, String chatId, List<UserGroup> userGroupList, String pageToken, GetChatMembersReq req) {
        req.setPageToken(pageToken);
        GetChatMembersResp resp;
        try {
            resp = CLIENT.im().chatMembers().get(req);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 通过机器人获取人员分页查询异常");
        }
        GetChatMembersRespBody data = resp.getData();
        ListMember[] dataItems = data.getItems();
        for (ListMember dataItem : dataItems) {
            UserMemgerInfo userMemgerInfo = new UserMemgerInfo();
            BeanUtil.copyProperties(dataItem, userMemgerInfo);
            lists.add(userMemgerInfo);
            addUserGroupList(chatId, userMemgerInfo, userGroupList);
        }
        if (data.getHasMore()) {
            pageToken = data.getPageToken();
            query(lists, chatId, userGroupList, pageToken, req);
        }
    }

    public List<ListMember> listUsersToChats(String chatId) {
        List<ListMember> result = new ArrayList<>();
        GetChatMembersReq req = GetChatMembersReq.newBuilder()
                .chatId(chatId)
                .memberIdType(GetChatMembersMemberIdTypeEnum.OPEN_ID)
                .pageSize(20)
                .build();
        GetChatMembersResp resp;
        boolean flag = true;
        while (flag) {
            try {
                resp = CLIENT.im().chatMembers().get(req);
                result.addAll(List.of(resp.getData().getItems()));
                flag = resp.getData().getHasMore();
                if (flag) {
                    req.setPageToken(resp.getData().getPageToken());
                }
            } catch (Exception e) {
                throw new FeishuException(e, this.getClass().getName() + " 通过机器人获取人员异常");
            }
        }
        return result;
    }

    public DeleteMessageReq deleteMessage(String messageId) {
        DeleteMessageReq req = DeleteMessageReq.newBuilder().messageId(messageId).build();
        try {
            DeleteMessageResp resp = CLIENT.im().message().delete(req, RequestOptions.newBuilder().build());
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 消息撤回异常");
        }
        return req;
    }
}
