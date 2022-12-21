package com.q.reminder.reminder.util;

import cn.hutool.core.bean.BeanUtil;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.docx.v1.enums.BatchUpdateDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.enums.PatchDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.model.*;
import com.lark.oapi.service.drive.v1.enums.BatchQueryMetaUserIdTypeEnum;
import com.lark.oapi.service.drive.v1.enums.UploadAllFileParentTypeEnum;
import com.lark.oapi.service.drive.v1.model.*;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.enums.GetChatMembersMemberIdTypeEnum;
import com.lark.oapi.service.im.v1.enums.ListChatUserIdTypeEnum;
import com.lark.oapi.service.im.v1.model.*;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetReq;
import com.lark.oapi.service.sheets.v3.model.QuerySpreadsheetSheetResp;
import com.lark.oapi.service.sheets.v3.model.Sheet;
import com.lark.oapi.service.wiki.v2.model.*;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.vo.*;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.FeishuJavaUtils
 * @Description :
 * @date :  2022.11.03 14:45
 */
@Log4j2
public abstract class FeishuJavaUtils {
    /**
     * 上传素材
     *
     * @param vo
     * @return
     */
    public static String upload(FeishuUploadImageVo vo) {
        String fileToken = null;
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        try {
            UploadAllMediaResp uploadAllMediaResp = client.drive().media().uploadAll(
                    UploadAllMediaReq.newBuilder().uploadAllMediaReqBody(UploadAllMediaReqBody.newBuilder()
                            .fileName(vo.getFileName())
                            .size(Math.toIntExact(vo.getSize()))
                            .parentNode(vo.getParentNode())
                            .parentType(vo.getParentType())
                            .file(vo.getFile())
                            .build()
                    ).build()
            );
            int code = uploadAllMediaResp.getCode();
            if (code == 0) {
                UploadAllMediaRespBody data = uploadAllMediaResp.getData();
                fileToken = data.getFileToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileToken;
    }

    /**
     * 更新块
     *
     * @param vo
     */
    public static Boolean updateBlocks(WeeklyProjectVo vo) {
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        UpdateBlockRequest update = UpdateBlockRequest.newBuilder().build();
        update.setReplaceImage(ReplaceImageRequest.newBuilder()
                .token(vo.getImageToken())
                .build());
        try {
            PatchDocumentBlockResp patch = client.docx().documentBlock().patch(PatchDocumentBlockReq.newBuilder()
                    .documentId(vo.getFileToken())
                    .blockId(vo.getBlockId())
                    .documentRevisionId(-1)
                    .userIdType(PatchDocumentBlockUserIdTypeEnum.USER_ID)
                    .updateBlockRequest(update)
                    .build());
            if (patch.getCode() == 0) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error(e);
        }
        return Boolean.FALSE;
    }

    /**
     * 更新块
     *
     * @param vo
     */
    public static Boolean batchUpdateBlocks(WeeklyProjectVo vo, UpdateBlockRequest[] updateBlockRequests) {
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();

        BatchUpdateDocumentBlockReq req = BatchUpdateDocumentBlockReq.newBuilder()
                .documentId(vo.getFileToken())
                .documentRevisionId(-1)
                .userIdType(BatchUpdateDocumentBlockUserIdTypeEnum.USER_ID)
                .batchUpdateDocumentBlockReqBody(BatchUpdateDocumentBlockReqBody.newBuilder().requests(updateBlockRequests).build())
                .build();
        try {
            BatchUpdateDocumentBlockResp resp = client.docx().documentBlock().batchUpdate(req);
            if (resp.getCode() == 0) {
                return Boolean.TRUE;
            } else {
                log.error("更新飞书周报失败，msg：{} error：{}", resp.getMsg(), resp.getError());
            }
        } catch (Exception e) {
            log.error(e);
        }
        return Boolean.FALSE;
    }

    /**
     * 发送飞书消息
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public static Boolean sendContent(ContentVo vo) throws Exception {
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType(vo.getMsgType())
                        .receiveId(vo.getReceiveId())
                        .content(vo.getContent())
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(CreateMessageReceiveIdTypeEnum.OPEN_ID).build();
        CreateMessageResp resp = client.im().message().create(req);
        if (resp.getCode() == 0) {
            return Boolean.TRUE;
        }
        log.error("发送飞书消息失败，msg：{} ;\r\n\terror：{}", resp.getMsg(), resp.getError());
        return Boolean.FALSE;
    }

    public static Boolean sendContent(MessageVo vo) throws Exception {
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType(vo.getMsgType())
                        .receiveId(vo.getReceiveId())
                        .content(vo.getContent())
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(vo.getReceiveIdTypeEnum()).build();
        CreateMessageResp resp = vo.getClient().im().message().create(req);
        if (resp.getCode() == 0) {
            return Boolean.TRUE;
        }
        log.error("发送飞书消息失败，msg：{} ;\r\n\terror：{}", resp.getMsg(), resp.getError());
        return Boolean.FALSE;
    }

    /**
     * 上传文件
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public static String uploadFile(FeishuUploadImageVo vo) throws Exception {
        File file = vo.getFile();
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        UploadAllFileReq req = UploadAllFileReq.newBuilder()
                .uploadAllFileReqBody(UploadAllFileReqBody.newBuilder()
                        .file(file)
                        .fileName(file.getName())
                        .parentNode(vo.getFolderToken())
                        .parentType(UploadAllFileParentTypeEnum.EXPLORER)
                        .size((int) file.length())
                        .build())
                .build();
        UploadAllFileResp resp = client.drive().file().uploadAll(req);
        return resp.getData().getFileToken();
    }

    public static Meta[] getDocx(ContentVo vo, RequestDoc[] doc) throws Exception {
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        BatchQueryMetaReq req = BatchQueryMetaReq.newBuilder()
                .metaRequest(MetaRequest.newBuilder()
                        .requestDocs(doc)
                        .withUrl(true)
                        .build())
                .userIdType(BatchQueryMetaUserIdTypeEnum.OPEN_ID).build();
        BatchQueryMetaResp resp = client.drive().meta().batchQuery(req);
        return resp.getData().getMetas();
    }

    /**
     * 消息上传文件
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public static String imUploadFile(ContentVo vo) throws Exception {
        File file = vo.getFile();
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        CreateFileReq req = CreateFileReq.newBuilder()
                .createFileReqBody(CreateFileReqBody.newBuilder()
                        .file(file)
                        .fileName(file.getName())
                        .fileType(vo.getFileType())
                        .build())
                .build();
        CreateFileResp resp = client.im().file().create(req);
        return resp.getData().getFileKey();
    }

    /**
     * 复制知识空间节点
     *
     * @param client
     * @param projectToken
     * @param title
     * @return
     * @throws Exception
     */
    public static Node syncSpacesWiki(Client client, String projectToken, String title) throws Exception {
        CopySpaceNodeReq req = CopySpaceNodeReq.newBuilder()
                .copySpaceNodeReqBody(CopySpaceNodeReqBody.newBuilder()
                        .targetParentToken(projectToken)
                        .targetSpaceId("7046680616087126018")
                        .title(title)
                        .build())
                .nodeToken("wikcnXpXCgmL3E7vdbM1TiwXiGc")
                .spaceId("7046680616087126018")
                .build();
        CopySpaceNodeResp resp = client.wiki().spaceNode().copy(req);
        return resp.getData().getNode();
    }

    /**
     * 获取电子表格sheets
     *
     * @param spreadsheetToken
     * @return
     */
    public static List<SheetVo> getSpredsheets(Client client, String spreadsheetToken) throws Exception {
        // 创建请求对象
        QuerySpreadsheetSheetReq req = QuerySpreadsheetSheetReq.newBuilder()
                .spreadsheetToken(spreadsheetToken)
                .build();
        // 发起请求
        QuerySpreadsheetSheetResp resp = client.sheets().spreadsheetSheet().query(req, RequestOptions.newBuilder().build());
        List<SheetVo> list = new ArrayList<>();
        Sheet[] sheets = resp.getData().getSheets();
        for (Sheet sheet : sheets) {
            SheetVo vo = new SheetVo();
            vo.setTitle(sheet.getTitle());
            vo.setSheetId(sheet.getSheetId());
            list.add(vo);
        }
        return list;
    }


    /**
     * 获取知识空间节点信息
     *
     * @param client
     * @param token
     * @return
     * @throws Exception
     */
    public static Node getSpacesNode(Client client, String token) throws Exception {
        GetNodeSpaceReq req = GetNodeSpaceReq.newBuilder()
                .token(token)
                .build();
        GetNodeSpaceResp resp = client.wiki().space().getNode(req);
        return resp.getData().getNode();
    }

    /**
     * 获取机器人所在群组
     *
     * @param client
     * @return
     * @throws Exception
     */
    public static List<GroupInfo> getGroupToChats(Client client) throws Exception {
        ListChatReq req = ListChatReq.newBuilder().userIdType(ListChatUserIdTypeEnum.OPEN_ID).build();
        ListChatResp resp = client.im().chat().list(req);
        ListChatRespBody data = resp.getData();
        ListChat[] items = data.getItems();
        ArrayList<ListChat> listChats = new ArrayList<>(Arrays.asList(items));
        List<GroupInfo> list = new ArrayList<>();
        listChats.forEach(e -> {
            GroupInfo groupInfo = new GroupInfo();
            BeanUtil.copyProperties(e, groupInfo);
            list.add(groupInfo);
        });
        return list;
    }

    public static List<UserMemgerInfo> getMembersByChats(Client client, List<GroupInfo> chats, List<UserGroup> userGroupList) throws Exception {
        List<UserMemgerInfo> items = new ArrayList<>();
        for (GroupInfo chat : chats) {
            String chatId = chat.getChatId();
            GetChatMembersReq req = GetChatMembersReq.newBuilder()
                    .chatId(chatId)
                    .memberIdType(GetChatMembersMemberIdTypeEnum.OPEN_ID)
                    .pageSize(20)
                    .build();
            GetChatMembersResp resp = client.im().chatMembers().get(req, RequestOptions.newBuilder().build());
            GetChatMembersRespBody data = resp.getData();
            for (ListMember dataItem : data.getItems()) {
                UserMemgerInfo userMemgerInfo = new UserMemgerInfo();
                BeanUtil.copyProperties(dataItem, userMemgerInfo);
                items.add(userMemgerInfo);
                addUserGroupList(chatId, userMemgerInfo, userGroupList);
            }
            String pageToken = data.getPageToken();
            if (data.getHasMore()) {
                query(items, chatId, userGroupList, pageToken, req, client);
            }
        }
        return items.stream().distinct().toList();
    }

    /**
     * 分页查询
     *
     * @param lists
     * @param chatId
     * @param userGroupList
     * @param pageToken
     * @param req
     * @param client
     */
    private static void query(List<UserMemgerInfo> lists, String chatId, List<UserGroup> userGroupList, String pageToken, GetChatMembersReq req, Client client) throws Exception {
        req.setPageToken(pageToken);
        GetChatMembersResp resp = client.im().chatMembers().get(req, RequestOptions.newBuilder().build());
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
            query(lists, chatId, userGroupList, pageToken, req, client);
        }
    }

    private static void addUserGroupList(String chatId, UserMemgerInfo e, List<UserGroup> userGroupList) {
        UserGroup ug = new UserGroup();
        ug.setChatId(chatId);
        ug.setMemberId(e.getMemberId());
        userGroupList.add(ug);
    }
}
