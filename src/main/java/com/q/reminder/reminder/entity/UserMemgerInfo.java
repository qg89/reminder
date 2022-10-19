package com.q.reminder.reminder.entity;

import com.alibaba.fastjson2.annotation.JSONField;
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
@TableName("fs_user_member_info")
@Data
public class UserMemgerInfo implements Serializable {
    private static final long serialVersionUID = 7510561412354805175L;

    /**
     * 员工ID
     */
    @JSONField(name = "member_id")
    @TableId(type = IdType.INPUT)
    private String memberId;
    private String name;
    @JSONField(name = "tenant_key")
    private String tenantKey;
    /**
     * 飞书类型ID
     */
    @JSONField(name = "member_id_type")
    private String memberIdType;

    /**
     * redmineID
     */
    private String rUserId;
    /**
     *
     */
    private String userName;
    @TableLogic(value = "0", delval = "1")
    private String isDelete;

}
