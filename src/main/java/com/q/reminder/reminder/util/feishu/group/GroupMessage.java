package com.q.reminder.reminder.util.feishu.group;

import cn.hutool.core.bean.BeanUtil;
import com.lark.oapi.service.im.v1.enums.GetChatMembersMemberIdTypeEnum;
import com.lark.oapi.service.im.v1.enums.ListChatUserIdTypeEnum;
import com.lark.oapi.service.im.v1.model.*;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
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

    private GroupMessage(){
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
    public List<GroupInfo> getGroupToChats() throws Exception {
        ListChatReq req = ListChatReq.newBuilder().userIdType(ListChatUserIdTypeEnum.OPEN_ID).build();
        ListChatResp resp = CLIENT.im().chat().list(req, REQUEST_OPTIONS);
        List<GroupInfo> list = new ArrayList<>();
        if (resp.success()) {
            ListChatRespBody data = resp.getData();
            ListChat[] items = data.getItems();
            ArrayList<ListChat> listChats = new ArrayList<>(Arrays.asList(items));
            listChats.forEach(e -> {
                GroupInfo groupInfo = new GroupInfo();
                BeanUtil.copyProperties(e, groupInfo);
                list.add(groupInfo);
            });
        }
        return list;
    }

    public List<UserMemgerInfo> getMembersByChats(List<GroupInfo> chats, List<UserGroup> userGroupList) throws Exception {
        List<UserMemgerInfo> items = new ArrayList<>();
        for (GroupInfo chat : chats) {
            String chatId = chat.getChatId();
            GetChatMembersReq req = GetChatMembersReq.newBuilder()
                    .chatId(chatId)
                    .memberIdType(GetChatMembersMemberIdTypeEnum.OPEN_ID)
                    .pageSize(20)
                    .build();
            GetChatMembersResp resp = CLIENT.im().chatMembers().get(req, REQUEST_OPTIONS);
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
    private void query(List<UserMemgerInfo> lists, String chatId, List<UserGroup> userGroupList, String pageToken, GetChatMembersReq req) throws Exception {
        req.setPageToken(pageToken);
        GetChatMembersResp resp = CLIENT.im().chatMembers().get(req, REQUEST_OPTIONS);
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
}
