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
 * @ClassName : com.q.reminder.reminder.entity.DepVo
 * @Description :
 * @date :  2022.09.27 08:40
 */
@Data
@TableName("fs_group_info")
public class GroupInfo implements Serializable {
    private static final long serialVersionUID = 6352091359792413686L;

    @JSONField(name = "chat_id")
    @TableId(type = IdType.INPUT)
    private String chatId;
    private String name;
    @JSONField(name = "owner_id")
    private String ownerId;
    @JSONField(name = "owner_id_type")
    private String ownerIdType;
    @TableLogic(value = "0", delval = "1")
    private String isDelete;
}
