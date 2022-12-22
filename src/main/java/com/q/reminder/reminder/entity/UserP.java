package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.UserP
 * @Description :
 * @date :  2022.12.21 19:26
 */
@Data
@AllArgsConstructor
@TableName("s_user_p")
public class UserP implements Serializable {

    @Serial
    private static final long serialVersionUID = -6158507322335711440L;

    @MppMultiId
    @TableField("user_id")
    private String userId;

    @MppMultiId
    @TableField("p_id")
    private String pId;
}
