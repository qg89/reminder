package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * redmine问题记录表(RdIssueBug)实体类
 *
 * @author makejava
 * @since 2023-05-11 18:18:54
 */
@Data
@TableName("rd_issue_bug")
public class RdIssueBug extends Model<RdIssueBug> implements Serializable {
    @Serial
    private static final long serialVersionUID = -49343050288841662L;

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

