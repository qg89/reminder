package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.User
 * @Description :
 * @date :  2022.11.17 14:09
 */
@Data
@TableName("s_user")
public class User extends Model<User> implements Serializable {

    @Serial
    private static final long serialVersionUID = -6402690812700891582L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("name")
    private String name;
    /**
     * 本地IP，逗号分隔
     */
    @TableField(value = "remote_addr")
    private String remoteAddr;

    @TableField(value = "enable")
    @TableLogic(value = "0", delval = "1")
    private Integer enable;
}
