package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * redmine 项目表(RProjectInfo)实体类
 *
 * @author makejava
 * @since 2023-03-06 10:45:31
 */
@Data
@TableName("r_project_info")
public class RProjectInfo extends Model<RProjectInfo> implements Serializable {
    @Serial
    private static final long serialVersionUID = -81733391617223332L;
    
        
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * redmine项目ID
     */     
     @TableField(value = "p_id")
     private String pid;
    /**
     * redmine项目key
     */
    @TableField(value = "p_key")
    private String pkey;
    /**
     * redmine项目名称
     */
    @TableField(value = "p_name")
    private String pname;

    /**
     * 项目预算（万元）
     */
    @TableField(value = "budget")
    private Double budget;

    /**
     * 人均成本（万元）
     */
    @TableField(value = "per_capita_cost")
    private Double perCapitaCost;

    /**
     * 利润率
     */
    @TableField(value = "profit_margin")
    private Double profitMargin;

    /**
     * 项目短名称
     */
    @TableField(value = "project_short_name")
    private String projectShortName;
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
     * 项目开始时间
     */     
     @TableField(value = "start_day")
     private Date startDay;
    /**
     * 发送群对应的ID
     */     
     @TableField(value = "send_group_chat_id")
     private String sendGroupChatId;
    /**
     * 是否发送需求群
     */     
     @TableField(value = "is_send_group")
     private String isSendGroup;
    /**
     * 产品经理飞书ID
     */     
     @TableField(value = "product_member_id")
     private String productMemberId;
    /**
     * 项目周报文件夹token
     */     
     @TableField(value = "folder_token")
     private String folderToken;
    /**
     * 项目周报是否复制：0复制，1不复制
     */     
     @TableField(value = "weekly_copy_type")
     private String weeklyCopyType;
    /**
     * 项目周报类型：0生成，1不生成
     */     
     @TableField(value = "weekly_type")
     private String weeklyType;
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
     @TableLogic(value = "0", delval = "1")
     private String isDelete;
         
     @TableField(value = "update_time")
     private Date updateTime;
         
     @TableField(value = "create_time")
     private Date createTime;


}

