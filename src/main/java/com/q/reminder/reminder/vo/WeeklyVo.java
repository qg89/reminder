package com.q.reminder.reminder.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.WeeklyVo
 * @Description :
 * @date :  2022.11.15 11:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WeeklyVo extends WeeklyProjectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7434225459549482768L;
    private String id;
    private String title;
    private Integer weekNum;
}
