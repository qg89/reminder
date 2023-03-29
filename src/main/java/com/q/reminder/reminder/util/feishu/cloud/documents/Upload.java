package com.q.reminder.reminder.util.feishu.cloud.documents;

import com.lark.oapi.service.drive.v1.model.UploadAllMediaReq;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReqBody;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaResp;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaRespBody;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.cloud.documents.Upload
 * @Description :
 * @date :  2023.03.27 16:45
 */
public class Upload extends BaseFeishu {

    private static Upload instance;

    private Upload() {
        super();
    }

    public static synchronized Upload getInstance() {
        if (instance == null) {
            instance = new Upload();
        }
        return instance;
    }

    /**
     * 上传素材
     *
     * @param vo
     * @return
     */
    public String uploadFile(FeishuUploadImageVo vo) {
        UploadAllMediaResp uploadAllMediaResp;
        try {
            uploadAllMediaResp = CLIENT.drive().media().uploadAll(
                    UploadAllMediaReq.newBuilder().uploadAllMediaReqBody(UploadAllMediaReqBody.newBuilder()
                            .fileName(vo.getFileName())
                            .size(Math.toIntExact(vo.getSize()))
                            .parentNode(vo.getParentNode())
                            .parentType(vo.getParentType())
                            .file(vo.getFile())
                            .build()
                    ).build()
                    , REQUEST_OPTIONS
            );
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 上传素材异常");
        }
        if (uploadAllMediaResp.success()) {
            UploadAllMediaRespBody data = uploadAllMediaResp.getData();
            return data.getFileToken();
        }
        return null;
    }
}
