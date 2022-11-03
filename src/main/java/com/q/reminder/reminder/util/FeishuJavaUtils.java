package com.q.reminder.reminder.util;

import com.lark.oapi.Client;
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
        String upload = Image.upload(vo);
        System.out.println(upload);
    }

    public static class Image {
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
    }
}
