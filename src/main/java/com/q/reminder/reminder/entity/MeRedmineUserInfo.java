package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * (MeRedmineUserInfo)实体类
 *
 * @author makejava
 * @since 2024-01-02 09:19:50
 */
@Data
@TableName("me_redmine_user_info")
public class MeRedmineUserInfo extends Model<MeRedmineUserInfo> implements Serializable {
    @Serial
    private static final long serialVersionUID = 763709980650021295L;


    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Integer id;


    @TableField(value = "username")
    private String username;


    @TableField(value = "password")
    private String password;


    @TableField(value = "api_key")
    private String apiKey;


    @TableField(value = "issue_id")
    private Integer issueId;


    @TableField(value = "project_id")
    private Integer projectId;


    @TableField(value = "name")
    private String name;


    @TableField(value = "spent_on", exist = false)
    private String spentOn;

    /**
     * 删除标识
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "is_delete")
    private String isDelete;


    @TableField(value = "update_time")
    private Date updateTime;


    @TableField(value = "create_time")
    private Date createTime;

}

