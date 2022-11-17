package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.UpdatePasswordVo
 * @Description :
 * @date :  2022.11.17 16:59
 */
@Data
public class UpdatePasswordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3204964215843602676L;
    private String username;
    private String password;
    private String newPd;
}
