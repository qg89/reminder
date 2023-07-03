package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * (SHolidayConfig)实体类
 *
 * @author makejava
 * @since 2023-07-03 17:27:33
 */
@Data
@TableName("s_holiday_config")
public class SHolidayConfig extends Model<SHolidayConfig> implements Serializable {
    @Serial
    private static final long serialVersionUID = 688891904430696646L;
    
        
    @TableId(type = IdType.AUTO)
    @TableField(value = "date")
    private Date date;
    

    /**
     * 是否工作日
     */     
    @TableField(value = "work")
    private Integer work;
     
         
    @TableField(value = "name")
    private String name;
     
}

