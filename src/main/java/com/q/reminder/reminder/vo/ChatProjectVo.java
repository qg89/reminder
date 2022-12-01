package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ChatProjectVo
 * @Description :
 * @date :  2022.12.01 14:28
 */
@Data
public class ChatProjectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6790347067498482012L;

    private String pId;
    private String chatId;
    private String reminderNone;
    private String reminderTitle;
    private String pKey;
    private String memberId;
    private String name;
}
