package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
    private String pId;
    private String pKey;
    private String pName;
    private String featureToken;
    private String redmineUrl;
    private String accessKey;
    /**
     * 是否发送需求群
     */
    private String isSendGroup;

    /**
     * 飞书群ID
     */
    private String sendGroupChatId;
    /**
     * 产品经理飞书ID
     */
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
