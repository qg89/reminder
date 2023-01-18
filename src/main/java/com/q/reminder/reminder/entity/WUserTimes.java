package com.q.reminder.reminder.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * (WUserTimes)实体类
 *
 * @author makejava
 * @since 2023-01-18 10:40:43
 */
@Data
@TableName("w_user_times")
public class WUserTimes extends Model<WUserTimes> implements Serializable {
    @Serial
    private static final long serialVersionUID = -95934262243607496L;
    
        
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * redmine 项目ID
     */    
    private String pId;
    /**
     * redmine 用户ID
     */    
    private String userId;
    /**
     * 年月日
     */    
    private String day;
    /**
     * 耗时
     */    
    private BigDecimal houses;
    /**
     * 工时类型：0 正常工时， 1BUG工时
     */    
    private String timeType;
        
    private Date updateTime;
        
    private Date createTime;


}

