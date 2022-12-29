package com.q.reminder.reminder.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * 工时配置(WConfig)实体类
 *
 * @author makejava
 * @since 2022-12-29 10:30:25
 */
@Data
@TableName("w_config")
public class WConfig extends Model<WConfig> implements Serializable {
    @Serial
    private static final long serialVersionUID = 209768548582610311L;
    
    /**
     * 主键
     */    
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 月
     */    
    private Integer dateId;
    /**
     * 当月工作天数
     */    
    private BigDecimal days;


}

