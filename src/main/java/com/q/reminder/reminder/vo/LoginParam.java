package com.q.reminder.reminder.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.LoginParam
 * @Description :
 * @date :  2022.11.17 14:12
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginParam implements Serializable {
    @Serial
    private static final long serialVersionUID = 3066787066015770722L;
    private String username;
    private String password;
}
