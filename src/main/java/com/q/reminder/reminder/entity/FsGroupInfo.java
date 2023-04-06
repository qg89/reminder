package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
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
public class FsGroupInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 6352091359792413686L;

    /**
     * 群组 ID
     */
    @TableId(type = IdType.INPUT)
    @TableField(value = "chat_id")
    private String chatId;
    /**
     * 群名称
     */
    @TableField(value = "name")
    private String name;
    /**
     * 群主 ID
     */
    @TableField(value = "owner_id")
    private String ownerId;
    /**
     * 群主 ID 类型


     */
    @TableField(value = "owner_id_type")
    private String ownerIdType;
    /**
     * 发送群消息类型：
     0，三部日常提醒
     1，redmine需求提醒
     2，coverity创建评审问题
     */
    @TableField(value = "send_type")
    private String sendType;
    /**
     * 提醒为空文案
     */
    @TableField(value = "reminder_none")
    private String reminderNone;
    /**
     * 提醒标题
     */
    @TableField(value = "reminder_title")
    private String reminderTitle;
    /**
     * 删除标识
     */
    @TableField(value = "is_delete")
    private String isDelete;
}
