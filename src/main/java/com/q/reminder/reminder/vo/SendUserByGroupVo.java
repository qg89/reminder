package com.q.reminder.reminder.vo;

import com.alibaba.fastjson2.JSONArray;
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
    private String reminderNone;
    private String assigneeId;
    private JSONArray reminderContent;
    private String reminderTitle;

}
