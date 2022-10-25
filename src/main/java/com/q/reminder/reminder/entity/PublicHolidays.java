package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.PublicHolidays
 * @Description :
 * @date :  2022.10.21 15:15
 */
@TableName("s_public_holidays")
@Data
public class PublicHolidays implements Serializable {
    private Date holiday;
    private String type;
}
