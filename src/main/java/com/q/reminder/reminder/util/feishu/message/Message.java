package com.q.reminder.reminder.util.feishu.message;

import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.*;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.util.RedisUtils;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.ContentVo;
import com.q.reminder.reminder.vo.MessageVo;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import tech.powerjob.worker.log.OmsLogger;

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
    public CreateMessageResp sendContent(MessageVo vo) {
        String key = RedisKeyContents.FEISHU_MESSAGE_INVOKEEXCEEDEDTIMES;
        RedisUtils redisUtils = RedisUtils.getInstance();
        String content = vo.getContent();
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType(vo.getMsgType())
                        .receiveId(vo.getReceiveId())
                        .content(content)
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(CreateMessageReceiveIdTypeEnum.OPEN_ID).build();
        CreateMessageResp resp = new CreateMessageResp();
        try {
            resp = CLIENT.im().message().create(req);
        } catch (Exception e) {
            if (!resp.success() && redisUtils.invokeExceededTimes(key, 10, 5)) {
                log.error("发送消息异常：error: {}, content:{}", resp.getMsg(), content);
                sendContent(vo);
            }
//            throw new FeishuException(e, this.getClass().getName() + " 发送消息异常");
        }
        if (!resp.success() && redisUtils.invokeExceededTimes(key, 10, 5)) {
            log.error("发送消息异常：fault: {}, content:{}", resp.getMsg(), content);
            sendContent(vo);
        }
        redisUtils.removeKey(key);
        return resp;
    }

    /**
     * 发送消息
     *
     * @param vo
     * @param log
     * @return
     */
    public CreateMessageResp sendContent(MessageVo vo, OmsLogger log) {
        String content = vo.getContent();
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType(vo.getMsgType())
                        .receiveId(vo.getReceiveId())
                        .content(content)
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(CreateMessageReceiveIdTypeEnum.OPEN_ID).build();
        return getCreateMessageResp(vo, log, content, req);
    }

    @NotNull
    private CreateMessageResp getCreateMessageResp(MessageVo vo, OmsLogger log, String content, CreateMessageReq req) {
        String key = RedisKeyContents.FEISHU_MESSAGE_INVOKEEXCEEDEDTIMES;
        RedisUtils redisUtils = RedisUtils.getInstance();
        CreateMessageResp resp = new CreateMessageResp();
        try {
            resp = this.CLIENT.im().message().create(req);
        } catch (Exception e) {
            if (!resp.success() && redisUtils.invokeExceededTimes(key, 10, 5)) {
                log.error("Task发送消息异常：error: {}, content:{}",  resp.getMsg(), content);
                sendContent(vo, log);
            }
//            throw new FeishuException(e, this.getClass().getName() + " Task发送消息异常");
        }
        if (!resp.success() && redisUtils.invokeExceededTimes(key, 10, 5)) {
            log.error("Task发送消息异常：error: {}, content:{}",  resp.getMsg(), content);
            sendContent(vo, log);
        }
        redisUtils.removeKey(key);
        return resp;
    }

    /**
     * 发送消息,文本
     *
     * @param vo
     * @param log
     * @return
     */
    public CreateMessageResp sendText(MessageVo vo, OmsLogger log) {
        JSONObject json = new JSONObject();
        json.put("text", vo.getContent());
        String content = vo.getContent();
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .msgType("text")
                        .receiveId(vo.getReceiveId())
                        .content(json.toJSONString())
                        .uuid(UUID.randomUUID().toString())
                        .build()).receiveIdType(CreateMessageReceiveIdTypeEnum.OPEN_ID).build();
        return getCreateMessageResp(vo, log, content, req);
    }

    /**
     * 消息上传文件
     *
     * @param vo
     * @return
     * @throws Exception
     */
    public String imUploadFile(ContentVo vo) {
        File file = vo.getFile();
        CreateFileReq req = CreateFileReq.newBuilder()
                .createFileReqBody(CreateFileReqBody.newBuilder()
                        .file(file)
                        .fileName(file.getName())
                        .fileType(vo.getFileType())
                        .build())
                .build();
        CreateFileResp resp;
        try {
            resp = CLIENT.im().file().create(req);
        } catch (Exception e) {
            throw new FeishuException(e, this.getClass().getName() + " 消息上传文件异常");
        }
        if (resp.success()) {
            return resp.getData().getFileKey();
        }
        return null;
    }
}
