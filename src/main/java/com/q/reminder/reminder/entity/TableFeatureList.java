package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 需求管理表列表(TableFeatureList)实体类
 *
 * @author makejava
 * @since 2023-10-24 13:48:51
 */
@Data
@TableName("t_table_feature_list")
public class TableFeatureList extends Model<TableFeatureList> implements Serializable {
    @Serial
    private static final long serialVersionUID = 555047359004229260L;
    
/**
     * 飞书多维表格记录ID
     */    
    @TableId(type = IdType.AUTO)
    @TableField(value = "record_id")
    private String recordId;
    

/**
     * redmineID
     */     
    @TableField(value = "redmine_id")
    private String redmineId;
     
/**
     * 项目Key
     */     
    @TableField(value = "project_key")
    private String projectKey;
     
/**
     * 模块
     */     
    @TableField(value = "module")
    private String module;
     
/**
     * 一级
     */     
    @TableField(value = "menu_one")
    private String menuOne;
     
/**
     * 二级
     */     
    @TableField(value = "menu_two")
    private String menuTwo;
     
/**
     * 三级
     */     
    @TableField(value = "menu_three")
    private String menuThree;
     
/**
     * 功能描述
     */     
    @TableField(value = "dscrptn")
    private String dscrptn;
     
/**
     * 需求类型
     */     
    @TableField(value = "feature_type")
    private String featureType;
     
/**
     * 需求状态
     */     
    @TableField(value = "feature_state")
    private String featureState;
     
/**
     * 状态
     */     
    @TableField(value = "state")
    private String state;
     
     
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "is_delete")
    private String isDelete;
     
     
    @TableField(value = "update_time")
    private Date updateTime;
     
     
    @TableField(value = "create_time")
    private Date createTime;
     
}

