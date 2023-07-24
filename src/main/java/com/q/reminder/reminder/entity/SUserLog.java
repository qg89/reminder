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
 * (SUserLog)实体类
 *
 * @author makejava
 * @since 2023-07-24 10:47:23
 */
@Data
@TableName("s_user_log")
public class SUserLog extends Model<SUserLog> implements Serializable {
    @Serial
    private static final long serialVersionUID = -85453066790379790L;
    
        
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Integer id;
    

    /**
     * 用户ID
     */     
    @TableField(value = "user_id")
    private Long userId;
     
    /**
     * 请求参数
     */     
    @TableField(value = "params")
    private String params;
     
    /**
     * 请求URL
     */     
    @TableField(value = "request_url")
    private String requestUrl;
     
         
    @TableField(value = "create_time")
    private Date createTime;
     
}

