package com.q.reminder.reminder.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * (RdIssue)实体类
 *
 * @author makejava
 * @since 2023-01-30 14:32:09
 */
@Data
@TableName("rd_issue")
public class RdIssue extends Model<RdIssue> implements Serializable {
    @Serial
    private static final long serialVersionUID = 369761702227195016L;
    
    /**
     * 任务ID
     */    
    @MppMultiId
    @TableField("id")
    private Integer id;
    /**
     * 项目ID
     */    
    @MppMultiId
    @TableField("projectId")
    private Integer projectid;
    /**
     * 主题
     */    
    @TableField("subject")
    private String subject;
    /**
     * 项目名称
     */    
    @TableField("projectName")
    private String projectName;
    /**
     * 跟踪ID
     */    
    @TableField("tracker")
    private Integer tracker;
    /**
     * 跟踪名称
     */    
    @TableField("trackerName")
    private String trackerName;
    /**
     * 开始日期
     */    
    @TableField("startDate")
    private Date startDate;
    /**
     * 计划完成日期
     */    
    @TableField("dueDate")
    private Date dueDate;
    /**
     * 创建日期
     */    
    @TableField("createdOn")
    private Date createdOn;
    /**
     * 最新更新时间
     */    
    @TableField("updatedOn")
    private Date updatedOn;
    /**
     * 任务完成进度
     */    
    @TableField("doneRatio")
    private Integer doneRatio;
    /**
     * 父任务ID
     */    
    @TableField("parentId")
    private Integer parentId;
    /**
     * 优先级ID
     */    
    @TableField("priorityId")
    private Integer priorityId;
    /**
     * 优先级名称
     */    
    @TableField("priorityText")
    private String priorityText;
    /**
     * 预期时间
     */    
    @TableField("estimatedHours")
    private Double estimatedHours;
    /**
     * 耗时
     */    
    @TableField("spentHours")
    private Double spentHours;
    /**
     * 指派者ID
     */    
    @TableField("assigneeId")
    private Integer assigneeId;
    /**
     * 指派者姓名
     */    
    @TableField("assigneeName")
    private String assigneeName;
    /**
     * 创建者ID
     */    
    @TableField("authorId")
    private Integer authorId;
    /**
     * 创建者名称
     */    
    @TableField("authorName")
    private String authorName;
    /**
     * 任务描述
     */    
    @TableField("description")
    private String description;
    /**
     * 任务关闭时间
     */    
    @TableField("closedOn")
    private Date closedOn;
    /**
     * 状态ID
     */    
    @TableField("statusId")
    private Integer statusId;
    /**
     * 状态名称
     */    
    @TableField("statusName")
    private String statusName;
    /**
     * 是否私有
     */    
    @TableField("privateIssue")
    private String privateIssue;
    /**
     * 拓展字段JSON
     */    
    @TableField("customField")
    private String customField;


}

