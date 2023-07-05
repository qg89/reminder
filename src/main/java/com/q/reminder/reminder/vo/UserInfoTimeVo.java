package com.q.reminder.reminder.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.UserInfoTimeVo
 * @Description :
 * @date :  2023/7/6 00:30
 */
@Data
public class UserInfoTimeVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -2667062703490045118L;

    private String userName;
    private String hours;
    private String pname;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String spentOn;
}
