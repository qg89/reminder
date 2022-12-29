package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * (WConfig)实体类
 *
 * @author makejava
 * @since 2022-12-28 09:29:24
 */
@Data
@AllArgsConstructor
@TableName("w_config")
public class WConfig extends Model<WConfig> implements Serializable {
    @Serial
    private static final long serialVersionUID = -71145042673633889L;
    
    /**
     * 主键
     */    
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 年
     */    
    private String year;
    /**
     * 月
     */    
    private String month;
    /**
     * 当月工作天数
     */    
    private Double days;


}

