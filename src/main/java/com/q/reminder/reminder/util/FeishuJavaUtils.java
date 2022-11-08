package com.q.reminder.reminder.util;

import com.lark.oapi.Client;
import com.lark.oapi.service.docx.v1.enums.PatchDocumentBlockUserIdTypeEnum;
import com.lark.oapi.service.docx.v1.model.PatchDocumentBlockReq;
import com.lark.oapi.service.docx.v1.model.PatchDocumentBlockResp;
import com.lark.oapi.service.docx.v1.model.ReplaceImageRequest;
import com.lark.oapi.service.docx.v1.model.UpdateBlockRequest;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReq;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReqBody;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaResp;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaRespBody;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.FeishuJavaUtils
 * @Description :
 * @date :  2022.11.03 14:45
 */
@Log4j2
public class FeishuJavaUtils {
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
                    UploadAllMediaReq.newBuilder()
                            .uploadAllMediaReqBody(UploadAllMediaReqBody.newBuilder()
                                    .fileName(vo.getFileName())
                                    .size(Math.toIntExact(vo.getSize()))
                                    .parentNode(vo.getParentNode())
                                    .parentType(vo.getParentType())
                                    .file(vo.getFile())
                                    .build()).build()
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

    public static FeishuUploadImageVo getImage(String file) {
        FeishuUploadImageVo vo = new FeishuUploadImageVo();
        try (FileInputStream fi = new FileInputStream(file)) {
            BufferedImage read = ImageIO.read(fi);
            int height = read.getHeight();
            int width = read.getWidth();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return vo;
    }
}
