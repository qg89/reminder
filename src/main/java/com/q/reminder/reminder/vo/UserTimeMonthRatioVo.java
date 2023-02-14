package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.UserTimeMonthRatioVo
 * @Description :
 * @date :  2023.01.05 10:04
 */
@Data
public class UserTimeMonthRatioVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2936166767815898626L;
    private String year;
    private String month;
    private String pId;
    private Double ratio;
    private Integer userId;
}
