package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.UserGroup
 * @Description :
 * @date :  2022.09.27 20:14
 */
@TableName("fs_user_group")
@Data
public class UserGroup implements Serializable {
    private static final long serialVersionUID = 2388537062682977582L;

    @MppMultiId
    @TableField("chat_id")
    private String chatId;
    @MppMultiId
    @TableField("member_id")
    private String memberId;
}
