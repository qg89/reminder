package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * (WRoleGroupUser)实体类
 *
 * @author makejava
 * @since 2022-12-28 10:04:07
 */
@Data
@AllArgsConstructor
@TableName("w_role_group_user")
public class WRoleGroupUser extends Model<WRoleGroupUser> implements Serializable {
    @Serial
    private static final long serialVersionUID = -35774101959759899L;
    
        
    @MppMultiId
    private Integer roleId;

    @MppMultiId
    private Integer groupId;

    @MppMultiId
    private Integer userId;


}

