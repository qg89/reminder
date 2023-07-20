package com.q.reminder.reminder.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.UserInfoVo
 * @Description :
 * @date :  2022.11.17 15:16
 */
@Data
public class UserInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -828643677505571791L;
    private String token;
    private String username;
    private String pKey;
    @JSONField(serialize = false)
    private String remoteAddr;
}
