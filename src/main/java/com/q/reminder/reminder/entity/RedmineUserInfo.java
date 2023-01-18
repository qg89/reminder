package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.RedmineUserInfo
 * @Description :
 * @date :  2022.10.27 16:17
 */
@TableName("r_redmine_user_info")
@Data
public class RedmineUserInfo implements Serializable {

    @TableField("assignee_id")
    @MppMultiId
    private Integer assigneeId;
    @TableField("assignee_name")

    private String assigneeName;
    @TableField("user_name")
    private String userName;

    @TableField("redmine_type")
    @MppMultiId
    private String redmineType;

    @TableField("is_delete")
    @TableLogic(value = "0", delval = "1")
    private String isDelete;


}
