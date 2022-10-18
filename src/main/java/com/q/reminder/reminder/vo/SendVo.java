package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.SendVo
 * @Description :
 * @date :  2022.10.18 15:50
 */
@Data
public class SendVo implements Serializable {

    private static final long serialVersionUID = 6319674156399631789L;
    private String memberId;
    private String assigneeName;
    private String content;

}
