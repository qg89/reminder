package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.UserInfoWrokVo
 * @Description :
 * @date :  2023/7/5 21:37
 */
@Data
public class UserInfoWrokVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5626373534712444979L;
    private String userId;
    private String userName;
    private Double totalTime;
    private Double overtime;
}
