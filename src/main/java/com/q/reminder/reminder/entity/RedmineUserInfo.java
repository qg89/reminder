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

    @TableField(value = "assignee_id", fill = FieldFill.INSERT_UPDATE)
    @MppMultiId
    private Integer assigneeId;
    @TableField(value = "assignee_name", fill = FieldFill.INSERT_UPDATE)
    private String assigneeName;

    @TableField(value = "user_name", fill = FieldFill.INSERT_UPDATE)
    private String userName;

    @TableField(value = "redmine_type", fill = FieldFill.INSERT_UPDATE)
    @MppMultiId
    private String redmineType;

    @TableField("is_delete")
    @TableLogic(value = "0", delval = "1")
    private String isDelete;


}
