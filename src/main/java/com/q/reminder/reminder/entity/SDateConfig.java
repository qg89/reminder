package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * 日期表(SDateConfig)实体类
 *
 * @author makejava
 * @since 2022-12-28 10:04:07
 */
@Data
@AllArgsConstructor
@TableName("s_date_config")
public class SDateConfig extends Model<SDateConfig> implements Serializable {
    @Serial
    private static final long serialVersionUID = 908732847589315958L;
    
        
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 年份
     */    
    private String year;
    /**
     * 月份
     */    
    private String month;


}

