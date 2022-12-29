package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.DateConfigVo
 * @Description :
 * @date :  2022.12.29 10:43
 */
@Data
public class DateConfigVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 5782040154835569964L;

    private String days;
    private String month;
}
