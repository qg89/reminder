package com.q.reminder.reminder.util.feishu.message;

import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.im.v1.ImService;
import com.lark.oapi.service.im.v1.model.*;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.ContentVo;
import com.q.reminder.reminder.vo.MessageVo;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.UUID;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.message.Message
 * @Description :
 * @date :  2023.02.23 11:50
 */
@Log4j2
public class Message extends BaseFeishu {

    private static Message instance;

    private Message() {
        super();
    }

    public static synchronized Message getInstance() {
        if (instance == null) {
            instance = new Message();
        }
        return instance;
    }


    /**
     * 发送消息
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public void sendContent(MessageVo vo) {
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType(vo.getMsgType())
                        .receiveId(vo.getReceiveId())
                        .content(vo.getContent())
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(vo.getReceiveIdTypeEnum()).build();
        CreateMessageResp resp = new CreateMessageResp();
        ImService.Message message = CLIENT.im().message();
        try {
            message.create(req, RequestOptions.newBuilder().tenantAccessToken(TENANT_ACCESS_TOKEN).build());
        } catch (Exception e) {
            int i = 0;
            while (!resp.success() && i <= 3) {
                i++;
                try {
                    resp = message.create(req);
                } catch (Exception ex) {
                    log.error("发送消息异常次数:【{}】：{}", i, ex);
                }
            }
            log.error("发送消息异常次数:【{}】：{}", i, e);
        }
    }

    /**
     * 消息上传文件
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public String imUploadFile(ContentVo vo) throws Exception {
        File file = vo.getFile();
        CreateFileReq req = CreateFileReq.newBuilder()
                .createFileReqBody(CreateFileReqBody.newBuilder()
                        .file(file)
                        .fileName(file.getName())
                        .fileType(vo.getFileType())
                        .build())
                .build();
        CreateFileResp resp = CLIENT.im().file().create(req, RequestOptions.newBuilder().tenantAccessToken(TENANT_ACCESS_TOKEN).build());
        return resp.getData().getFileKey();
    }
}
