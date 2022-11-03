package com.q.reminder.reminder.util;

import com.lark.oapi.Client;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReq;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaReqBody;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaResp;
import com.lark.oapi.service.drive.v1.model.UploadAllMediaRespBody;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.FeishuUploadImage
 * @Description :
 * @date :  2022.11.03 11:38
 */
public class FeishuUploadImage {

    public static void upload(String appId, String appSecret) {
        Client client = Client.newBuilder(appId, appSecret).build();
        try {
            client.drive().media().uploadAll(
                    UploadAllMediaReq.newBuilder()
                            .uploadAllMediaReqBody(UploadAllMediaReqBody.newBuilder()
                                    .fileName("")
                                    .size(1)
                                    .parentNode("")
                                    .parentType("docx_image")
                                    .file(new File(""))
                                    .build()).build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        upload();
    }
}
