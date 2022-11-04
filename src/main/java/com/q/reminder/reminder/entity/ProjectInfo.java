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
 * @ClassName : com.q.reminder.reminder.entity.ProjectInfo
 * @Description :
 * @date :  2022.09.27 13:56
 */
@Data
@TableName("r_project_info")
public class ProjectInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 6061440968825915105L;
    @TableId(type = IdType.INPUT)
    @TableField("p_id")
    private String pId;

    @TableField("p_key")
    private String pKey;

    @TableField("p_name")
    private String pName;

    @TableField("feature_token")
    private String featureToken;

    @TableField("redmine_url")
    private String redmineUrl;

    @TableField("access_key")
    private String accessKey;
    /**
     * 是否发送需求群
     */
    @TableField("is_send_group")
    private String isSendGroup;

    /**
     * 飞书群ID
     */
    @TableField("send_group_chat_id")
    private String sendGroupChatId;
    /**
     * 产品经理飞书ID
     */
    @TableField("product_member_id")
    private String productMemberId;

    /**
     * 项目周报项目短名称
     */
    @TableField("project_short_name")
    private String projectShortName;

    /**
     * 周报文件夹token
     */
    @TableField("folder_token")
    private String folderToken;
}
