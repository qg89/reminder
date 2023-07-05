package com.q.reminder.reminder.vo.params;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.params.UserInfoParamsVo
 * @Description :
 * @date :  2023/7/5 21:34
 */
@Data
public class UserInfoParamsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8241766134223672473L;

    public String startTime;
    public String endTime;
    public String pid;
    public String userId;
}
