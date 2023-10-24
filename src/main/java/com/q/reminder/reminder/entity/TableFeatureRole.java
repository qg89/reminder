package com.q.reminder.reminder.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;



/**
 * 需求管理表各角色列表(TableFeatureRole)实体类
 *
 * @author makejava
 * @since 2023-10-24 11:42:46
 */
@Data
@TableName("t_table_feature_role")
public class TableFeatureRole extends Model<TableFeatureRole> implements Serializable {
    @Serial
    private static final long serialVersionUID = -11976059675978599L;
    
    
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Integer id;
    

/**
     * 飞书多维表格记录ID
     */     
    @TableField(value = "record_id")
    private String recordId;
     
/**
     * redmineID
     */     
    @TableField(value = "redmine_id")
    private String redmineId;
     
/**
     * 角色工时
     */     
    @TableField(value = "role_time")
    private String roleTime;
     
/**
     * 角色类型：1大数据，2后端，3前端，4测试，5算法
     */     
    @TableField(value = "role_type")
    private String roleType;
     
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

