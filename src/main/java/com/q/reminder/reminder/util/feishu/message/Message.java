package com.q.reminder.reminder.util.feishu.message;

import com.lark.oapi.service.im.v1.model.*;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.ContentVo;
import com.q.reminder.reminder.vo.MessageVo;

import java.io.File;
import java.util.UUID;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.message.Message
 * @Description :
 * @date :  2023.02.23 11:50
 */
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
    public Boolean sendContent(MessageVo vo) throws Exception {
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType(vo.getMsgType())
                        .receiveId(vo.getReceiveId())
                        .content(vo.getContent())
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(vo.getReceiveIdTypeEnum()).build();
        CreateMessageResp resp = CLIENT.im().message().create(req);
        if (resp.success()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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
        CreateFileResp resp = CLIENT.im().file().create(req);
        return resp.getData().getFileKey();
    }
}
