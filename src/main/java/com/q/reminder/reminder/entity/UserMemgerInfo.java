package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.User
 * @Description :
 * @date :  2022.09.23 14:30
 */
@TableName("user_member_info")
@Data
public class UserMemgerInfo implements Serializable {
    private static final long serialVersionUID = 7510561412354805175L;
    @TableId(type = IdType.INPUT)
    private String memberId;
    private String rUserId;
    private String userName;
    private String name;
    private String tenantKey;
    private String memberIdType;
    @TableLogic(value = "0", delval = "1")
    private String isDelete;

}
