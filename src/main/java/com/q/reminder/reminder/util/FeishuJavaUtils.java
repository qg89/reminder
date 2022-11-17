package com.q.reminder.reminder.util;

import com.lark.oapi.Client;
import com.lark.oapi.service.docx.v1.enums.BatchUpdateDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.enums.PatchDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.model.*;
import com.lark.oapi.service.drive.v1.enums.BatchQueryMetaUserIdTypeEnum;
import com.lark.oapi.service.drive.v1.enums.UploadAllFileParentTypeEnum;
import com.lark.oapi.service.drive.v1.model.*;
import com.lark.oapi.service.im.v1.enums.CreateFileFileTypeEnum;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.*;
import com.q.reminder.reminder.vo.ContentVo;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    /**
     * 上传文件
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
                .build();;
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
                        .fileType(vo.getMsgType())
                        .build())
                .build();
        CreateFileResp resp = client.im().file().create(req);
        return resp.getData().getFileKey();
    }
}
