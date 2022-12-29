package com.q.reminder.reminder.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * (WUserTimeMonth)实体类
 *
 * @author makejava
 * @since 2022-12-28 10:33:12
 */
@Data
@AllArgsConstructor
@TableName("w_user_time_month")
public class WUserTimeMonth extends Model<WUserTimeMonth> implements Serializable {
    @Serial
    private static final long serialVersionUID = -79256974408789613L;
    
        
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * redmine用户ID
     */    
    private Integer userId;
    /**
     * 日期表主键
     */    
    private Integer dateId;
    /**
     * 投入工时
     */    
    private BigDecimal times;


}

