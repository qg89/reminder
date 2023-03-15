package com.q.reminder.reminder.util;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.bitable.v1.enums.BatchCreateAppTableRecordUserIdTypeEnum;
import com.lark.oapi.service.bitable.v1.enums.BatchUpdateAppTableRecordUserIdTypeEnum;
import com.lark.oapi.service.bitable.v1.enums.ListAppTableRecordUserIdTypeEnum;
import com.lark.oapi.service.bitable.v1.model.*;
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
import com.q.reminder.reminder.constant.MsgTypeConstants;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.vo.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

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
public abstract class BaseFeishuJavaUtils {
    private static Client CLIENT = null;
    /**
     * 上传素材
     *
     * @param vo
     * @return
     */
    public static String upload(FeishuUploadImageVo vo) {
        String fileToken = null;
        if (CLIENT == null) {
            CLIENT = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        }
        try {
            UploadAllMediaResp uploadAllMediaResp = CLIENT.drive().media().uploadAll(
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
    @Deprecated
    public static Boolean updateBlocks(WeeklyProjectVo vo) {
        if (CLIENT == null) {
            CLIENT = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        }
        UpdateBlockRequest update = UpdateBlockRequest.newBuilder().build();
        update.setReplaceImage(ReplaceImageRequest.newBuilder()
                .token(vo.getImageToken())
                .build());
        try {
            PatchDocumentBlockResp patch = CLIENT.docx().documentBlock().patch(PatchDocumentBlockReq.newBuilder()
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
    @Deprecated
    public static Boolean batchUpdateBlocks(WeeklyProjectVo vo, UpdateBlockRequest[] updateBlockRequests) {
        if (CLIENT == null) {
            CLIENT = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        }
        BatchUpdateDocumentBlockReq req = BatchUpdateDocumentBlockReq.newBuilder()
                .documentId(vo.getFileToken())
                .documentRevisionId(-1)
                .userIdType(BatchUpdateDocumentBlockUserIdTypeEnum.USER_ID)
                .batchUpdateDocumentBlockReqBody(BatchUpdateDocumentBlockReqBody.newBuilder().requests(updateBlockRequests).build())
                .build();
        try {
            BatchUpdateDocumentBlockResp resp = CLIENT.docx().documentBlock().batchUpdate(req);
            if (resp.getCode() == 0) {
                return Boolean.TRUE;
            } else {
                JSONObject json = new JSONObject();
                json.put("text", "项目名称： " + vo.getProjectShortName() + "，msg：" + resp.getMsg() + ", error：" + resp.getError());
                sendAdmin(CLIENT, json, List.of("ou_35e03d4d8754dd35fed26c26849c85ab"));
            }
        } catch (Exception e) {
            log.error(e);
        }
        return Boolean.FALSE;
    }

    /**
     * 发送管理员
     *
     * @param client
     * @param json
     * @param adminInfos
     * @throws Exception
     */
    public static void sendAdmin(Client client, JSONObject json, List<String> adminInfos) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        adminInfos.forEach(memberId -> {
            MessageVo messageVo = new MessageVo();
            messageVo.setContent(json.toJSONString());
            messageVo.setReceiveId(memberId);
            messageVo.setMsgType(MsgTypeConstants.TEXT);
            messageVo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
            try {
                ContentVo vo = new ContentVo();
                vo.setContent(json.toJSONString());
                vo.setReceiveId(memberId);
                vo.setMsgType(MsgTypeConstants.TEXT);
                sendContent(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 发送飞书消息
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public static Boolean sendContent(ContentVo vo) throws Exception {
        if (CLIENT == null) {
            CLIENT = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        }
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType(vo.getMsgType())
                        .receiveId(vo.getReceiveId())
                        .content(vo.getContent())
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(CreateMessageReceiveIdTypeEnum.OPEN_ID).build();
        CreateMessageResp resp = CLIENT.im().message().create(req);
        if (resp.getCode() == 0) {
            return Boolean.TRUE;
        }
        log.error("发送飞书消息失败，msg：{} ;\r\n\terror：{}", resp.getMsg(), resp.getError());
        return Boolean.FALSE;
    }

    @Deprecated
    public static Boolean sendContent(MessageVo vo) throws Exception {
        if (CLIENT == null) {
            CLIENT = vo.getClient();
        }
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType(vo.getMsgType())
                        .receiveId(vo.getReceiveId())
                        .content(vo.getContent())
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(vo.getReceiveIdTypeEnum()).build();
        CreateMessageResp resp = CLIENT.im().message().create(req);
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
    @Deprecated
    public static String uploadFile(FeishuUploadImageVo vo) throws Exception {
        File file = vo.getFile();
        if (CLIENT == null) {
            CLIENT = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        }
        UploadAllFileReq req = UploadAllFileReq.newBuilder()
                .uploadAllFileReqBody(UploadAllFileReqBody.newBuilder()
                        .file(file)
                        .fileName(file.getName())
                        .parentNode(vo.getFolderToken())
                        .parentType(UploadAllFileParentTypeEnum.EXPLORER)
                        .size((int) file.length())
                        .build())
                .build();
        UploadAllFileResp resp = CLIENT.drive().file().uploadAll(req);
        return resp.getData().getFileToken();
    }

    @Deprecated
    public static Meta[] getDocx(ContentVo vo, RequestDoc[] doc) throws Exception {
        if (CLIENT == null) {
            CLIENT = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        }
        BatchQueryMetaReq req = BatchQueryMetaReq.newBuilder()
                .metaRequest(MetaRequest.newBuilder()
                        .requestDocs(doc)
                        .withUrl(true)
                        .build())
                .userIdType(BatchQueryMetaUserIdTypeEnum.OPEN_ID).build();
        BatchQueryMetaResp resp = CLIENT.drive().meta().batchQuery(req);
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
        if (CLIENT == null) {
            CLIENT = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        }
        CreateFileReq req = CreateFileReq.newBuilder()
                .createFileReqBody(CreateFileReqBody.newBuilder()
                        .file(file)
                        .fileName(file.getName())
                        .fileType(vo.getFileType())
                        .build())
                .build();
        CreateFileResp resp = CLIENT.im().file().create(req);
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
    @Deprecated
    public static Node syncSpacesWiki(Client client, String projectToken, String title) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        CopySpaceNodeReq req = CopySpaceNodeReq.newBuilder()
                .copySpaceNodeReqBody(CopySpaceNodeReqBody.newBuilder()
                        .targetParentToken(projectToken)
                        .targetSpaceId("7046680616087126018")
                        .title(title)
                        .build())
                .nodeToken("wikcnXpXCgmL3E7vdbM1TiwXiGc")
                .spaceId("7046680616087126018")
                .build();
        CopySpaceNodeResp resp = CLIENT.wiki().spaceNode().copy(req);
        return resp.getData().getNode();
    }

    /**
     * 获取电子表格sheets
     *
     * @param spreadsheetToken
     * @return
     */
    @Deprecated
    public static List<SheetVo> getSpredsheets(Client client, String spreadsheetToken) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        // 创建请求对象
        QuerySpreadsheetSheetReq req = QuerySpreadsheetSheetReq.newBuilder()
                .spreadsheetToken(spreadsheetToken)
                .build();
        // 发起请求
        QuerySpreadsheetSheetResp resp = CLIENT.sheets().spreadsheetSheet().query(req, RequestOptions.newBuilder().build());
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
    @Deprecated
    public static Node getSpacesNode(Client client, String token) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        GetNodeSpaceReq req = GetNodeSpaceReq.newBuilder()
                .token(token)
                .build();
        GetNodeSpaceResp resp = CLIENT.wiki().space().getNode(req);
        return resp.getData().getNode();
    }

    /**
     * 获取机器人所在群组
     *
     * @param client
     * @return
     * @throws Exception
     */
    @Deprecated
    public static List<GroupInfo> getGroupToChats(Client client) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        ListChatReq req = ListChatReq.newBuilder().userIdType(ListChatUserIdTypeEnum.OPEN_ID).build();
        ListChatResp resp = CLIENT.im().chat().list(req);
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

    @Deprecated
    public static List<UserMemgerInfo> getMembersByChats(Client client, List<GroupInfo> chats, List<UserGroup> userGroupList) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        List<UserMemgerInfo> items = new ArrayList<>();
        for (GroupInfo chat : chats) {
            String chatId = chat.getChatId();
            GetChatMembersReq req = GetChatMembersReq.newBuilder()
                    .chatId(chatId)
                    .memberIdType(GetChatMembersMemberIdTypeEnum.OPEN_ID)
                    .pageSize(20)
                    .build();
            GetChatMembersResp resp = CLIENT.im().chatMembers().get(req, RequestOptions.newBuilder().build());
            GetChatMembersRespBody data = resp.getData();
            for (ListMember dataItem : data.getItems()) {
                UserMemgerInfo userMemgerInfo = new UserMemgerInfo();
                BeanUtil.copyProperties(dataItem, userMemgerInfo);
                items.add(userMemgerInfo);
                addUserGroupList(chatId, userMemgerInfo, userGroupList);
            }
            String pageToken = data.getPageToken();
            if (data.getHasMore()) {
                query(items, chatId, userGroupList, pageToken, req, CLIENT);
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
    @Deprecated
    private static void query(List<UserMemgerInfo> lists, String chatId, List<UserGroup> userGroupList, String pageToken, GetChatMembersReq req, Client client) throws Exception {
        req.setPageToken(pageToken);
        if (CLIENT == null) {
            CLIENT = client;
        }
        GetChatMembersResp resp = CLIENT.im().chatMembers().get(req, RequestOptions.newBuilder().build());
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
            query(lists, chatId, userGroupList, pageToken, req, CLIENT);
        }
    }

    private static void addUserGroupList(String chatId, UserMemgerInfo e, List<UserGroup> userGroupList) {
        UserGroup ug = new UserGroup();
        ug.setChatId(chatId);
        ug.setMemberId(e.getMemberId());
        userGroupList.add(ug);
    }

    /**
     * 多为表格-记录-列出视图记录
     *
     * @param client
     * @return
     */
    @Deprecated
    public static List<AppTableRecord> listTableRecords(Client client, TTableInfo vo) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        List<AppTableRecord> resList = new ArrayList<>();
        ListAppTableRecordReq req = ListAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .viewId(vo.getViewId())
                .filter(vo.getFilter())
                .userIdType(ListAppTableRecordUserIdTypeEnum.OPEN_ID)
                .build();
        ListAppTableRecordResp resp;
        ListAppTableRecordRespBody respData = new ListAppTableRecordRespBody();
        do {
            String pageToken = respData.getPageToken();
            if (StringUtils.isNotBlank(pageToken)) {
                req.setPageToken(pageToken);
            }
            resp = CLIENT.bitable().appTableRecord().list(req);
            if (resp.getCode() != 0) {
                return resList;
            }
            respData = resp.getData();
            AppTableRecord[] items = respData.getItems();
            if (items == null) {
                return resList;
            }
            resList.addAll(Arrays.stream(items).toList());
        } while (resp.getCode() == 0 && respData.getHasMore());
        return resList;
    }

    /**
     * 多为表格-记录-批量创建记录
     *
     * @param client
     * @param vo
     * @throws Exception
     */
    @Deprecated
    public static void batchCreateTableRecords(Client client, TTableInfo vo, AppTableRecord[] records) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        BatchCreateAppTableRecordReq req = BatchCreateAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .userIdType(BatchCreateAppTableRecordUserIdTypeEnum.OPEN_ID)
                .batchCreateAppTableRecordReqBody(BatchCreateAppTableRecordReqBody.newBuilder()
                        .records(records)
                        .build())
                .build();
        BatchCreateAppTableRecordResp resp = CLIENT.bitable().appTableRecord().batchCreate(req);
        log.info("[多维表格]-保存数据：[状态] {}， [msg] {}", resp.getCode(), resp.getMsg());
    }

    /**
     * 多为表格-记录-批量更新记录
     *
     * @param client
     * @param vo
     * @throws Exception
     */
    @Deprecated
    public static void batchUpdateTableRecords(Client client, TTableInfo vo, AppTableRecord[] records) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        BatchUpdateAppTableRecordReqBody reqBody = BatchUpdateAppTableRecordReqBody.newBuilder().records(records).build();
        log.info("[多维表格]-Batch更新数据：{}", JSONObject.toJSONString(reqBody));
        BatchUpdateAppTableRecordReq req = BatchUpdateAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .userIdType(BatchUpdateAppTableRecordUserIdTypeEnum.OPEN_ID)
                .batchUpdateAppTableRecordReqBody(reqBody)
                .build();
        BatchUpdateAppTableRecordResp resp = CLIENT.bitable().appTableRecord().batchUpdate(req);
        if (!resp.success()) {
            log.info("[多维表格]-Batch更新数据：[状态] {}， [msg] {}", resp.getCode(), resp.getMsg());
        }
    }

    /**
     * 多为表格-记录-批量删除记录
     *
     * @param client
     * @param records
     */
    @Deprecated
    public static void batchDeleteTableRecords(Client client, TTableInfo vo, String[] records) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        // 创建请求对象
        BatchDeleteAppTableRecordReq req = BatchDeleteAppTableRecordReq.newBuilder()
                .appToken(vo.getAppToken())
                .tableId(vo.getTableId())
                .batchDeleteAppTableRecordReqBody(BatchDeleteAppTableRecordReqBody.newBuilder().records(records).build())
                .build();
        CLIENT.bitable().appTableRecord().batchDelete(req);
    }

    /**
     * 知识空间获取文件详情
     *
     * @param client
     * @param wikiToken
     * @return
     * @throws Exception
     */
    @Deprecated
    public static Node getNodeSpace(Client client, String wikiToken) throws Exception {
        if (CLIENT == null) {
            CLIENT = client;
        }
        GetNodeSpaceReq req = GetNodeSpaceReq.newBuilder()
                .token(wikiToken)
                .build();
        GetNodeSpaceResp resp = CLIENT.wiki().space().getNode(req, RequestOptions.newBuilder()
                .build());
        if (resp.success()) {
            return resp.getData().getNode();
        }
        return null;
    }



    public static void main(String[] args) throws Exception {
//        TTableInfo vo = new TTableInfo();
//        vo.setTableId("tbld61CFebNfZ6M6");
//        vo.setAppToken("bascnrkdLGoUftLgM7fvME7ly5c");
        if (CLIENT == null) {
            CLIENT = Client.newBuilder("cli_a1144b112738d013", "AQHvpoTxE4pxjkIlcOwC1bEMoJMkJiTx").build();;
        }

    }
}
