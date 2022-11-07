package com.q.reminder.reminder.util;

import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.docx.v1.model.PatchDocumentBlockReq;
import com.lark.oapi.service.docx.v1.model.PatchDocumentBlockResp;
import com.lark.oapi.service.docx.v1.model.ReplaceImageRequest;
import com.lark.oapi.service.docx.v1.model.UpdateBlockRequest;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReq;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReqBody;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaResp;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaRespBody;
import com.q.reminder.reminder.vo.FeishuUploadImageVo;

import java.io.File;


/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.FeishuJavaUtils
 * @Description :
 * @date :  2022.11.03 14:45
 */
public class FeishuJavaUtils {
    public static void main(String[] args) {
        FeishuUploadImageVo vo = new FeishuUploadImageVo();
        vo.setFile(new File("d:\\Users\\saiko\\Pictures\\chrome_pPdjQOcMKR.png"));
        vo.setParentType("docx_image");
        vo.setFileName("chrome_pPdjQOcMKR");
        vo.setParentNode("WoeIdKeGAoSoYgxWMzTciySRn3e");
        vo.setSize(29081);
        vo.setAppId("cli_a1144b112738d013");
        vo.setAppSecret("AQHvpoTxE4pxjkIlcOwC1bEMoJMkJiTx");
        String upload = upload(vo);
        System.out.println(upload);
    }

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
                                    .size(vo.getSize())
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
     * @param vo
     */
    public static void updateBlocks(FeishuUploadImageVo vo) {
        Client client = Client.newBuilder(vo.getAppId(), vo.getAppSecret()).build();
        UpdateBlockRequest update = UpdateBlockRequest.newBuilder().build();
        update.setReplaceImage(ReplaceImageRequest.newBuilder()
                .token("boxcn62J4gU0QSZRRiYOLMN10pg")
                .build());
        try {
            PatchDocumentBlockResp patch = client.docx().documentBlock().patch(PatchDocumentBlockReq.newBuilder()
                    .documentId("EVPzdLhwwosGcExpF3Pc37M6nDb")
                    .blockId("doxcnCoCO07lGgap7pxrPhwHOwe")
                    .updateBlockRequest(update)
                    .build());
            System.out.println(Jsons.DEFAULT.toJson(patch.getData()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
