package com.q.reminder.reminder.vo;

import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.MessageVo
 * @Description :
 * @date :  2022.12.01 11:10
 */
@Data
public class MessageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1403735921910913274L;
    private String receiveId;
    private String content;
    private String msgType;
    private CreateMessageReceiveIdTypeEnum receiveIdTypeEnum;
}
