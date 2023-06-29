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
 * redmine、飞书用户表关系表(FsUserMemberInfoTmp)实体类
 *
 * @author makejava
 * @since 2023-06-29 10:52:06
 */
@Data
@TableName("fs_user_member_info_tmp")
public class FsUserMemberInfoTmp extends Model<FsUserMemberInfoTmp> implements Serializable {
    @Serial
    private static final long serialVersionUID = 858370915248166979L;
    
    /**
     * 飞书用户ID
     */    
    @TableId(type = IdType.AUTO)
    @TableField(value = "member_id")
    private String memberId;
    

    /**
     * 飞书用户名称
     */     
    @TableField(value = "name")
    private String name;
     
    /**
     * 为租户在飞书上的唯一标识，用来换取对应的tenant_access_token，也可以用作租户在应用里面的唯一标识
     */     
    @TableField(value = "tenant_key")
    private String tenantKey;
     
    /**
     * 飞书用户ID类型
     */     
    @TableField(value = "member_id_type")
    private String memberIdType;
     
         
    @TableField(value = "create_time")
    private Date createTime;
     
}

