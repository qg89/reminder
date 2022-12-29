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
 * @since 2022-12-28 11:33:59
 */
@Data
@TableName("w_user_times")
public class WUserTimes extends Model<WUserTimes> implements Serializable {
    @Serial
    private static final long serialVersionUID = -56179210358790512L;
    
        
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


}

