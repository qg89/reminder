package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.SendUserByGroupVo
 * @Description :
 * @date :  2022.10.21 13:58
 */
@Data
public class SendUserByGroupVo implements Serializable {

    private static final long serialVersionUID = -4346456365733315964L;
    private String name;
    private String memberId;
    private String chatId;
    private String chatName;
    private String reminderNone;
    private String assigneeId;
    private String reminderTitle;

}
