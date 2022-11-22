package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private Integer assigneeId;
    private String assigneeName;
    private String redmineType;
    @TableLogic(value = "0", delval = "1")
    private String isDelete;


}
