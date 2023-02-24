package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * redmine 项目表(RProjectInfo)实体类
 *
 * @author makejava
 * @since 2023-02-24 14:16:44
 */
@Data
@TableName("r_project_info")
public class RProjectInfo extends Model<RProjectInfo> implements Serializable {
    @Serial
    private static final long serialVersionUID = -36380413822784542L;


    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;
    /**
     * redmine项目ID
     */
    @TableField(value = "p_id")
    private String pId;
    /**
     * redmine项目key
     */
    @TableField(value = "p_key")
    private String pKey;
    /**
     * redmine项目名称
     */
    @TableField(value = "p_name")
    private String pName;
    /**
     * 需求管理表token
     */
    @TableField(value = "feature_token")
    private String featureToken;
    /**
     * 同步需求管理表
     */
    @TableField(value = "sync_feature")
    private String syncFeature;
    /**
     * 项目短名称
     */
    @TableField(value = "project_short_name")
    private String projectShortName;
    /**
     * 项目周报文件夹token
     */
    @TableField(value = "folder_token")
    private String folderToken;
    /**
     * 项目经理Open_Id
     */
    @TableField(value = "pm_ou")
    private String pmOu;
    /**
     * 项目经理redminekey
     */
    @TableField(value = "pm_key")
    private String pmKey;
    /**
     * redmineURL
     */
    @TableField(value = "redmine_url")
    private String redmineUrl;
    /**
     * 项目开始时间
     */
    @TableField(value = "start_day")
    private Date startDay;
    /**
     * 是否发送需求群
     */
    @TableField(value = "is_send_group")
    private String isSendGroup;
    /**
     * 发送群对应的ID
     */
    @TableField(value = "send_group_chat_id")
    private String sendGroupChatId;
    /**
     * 产品经理飞书ID
     */
    @TableField(value = "product_member_id")
    private String productMemberId;
    /**
     * 生成状态，0生成，1不生成
     */
    @TableField(value = "wiki_type")
    private String wikiType;
    /**
     * 知识库token
     */
    @TableField(value = "wiki_token")
    private String wikiToken;
    /**
     * redmine类型:1旧，2新
     */
    @TableField(value = "redmine_type")
    private String redmineType;
    /**
     * 删除标识
     */
    @TableField(value = "is_delete")
    private String isDelete;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "create_time")
    private Date createTime;


}

