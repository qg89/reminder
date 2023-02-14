package com.q.reminder.reminder.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * (RdTimeEntry)实体类
 *
 * @author makejava
 * @since 2023-01-19 11:47:39
 */
@Data
@TableName("rd_time_entry")
public class RdTimeEntry extends Model<RdTimeEntry> implements Serializable {
    @Serial
    private static final long serialVersionUID = 711882528067511960L;

    @MppMultiId
    private Integer id;

    @MppMultiId
    @TableField("issueId")
    private Integer issueId;

    @MppMultiId
    @TableField("projectId")
    private Integer projectId;

    @TableField("projectName")
    private String projectName;

    @TableField("userName")
    private String userName;

    @TableField("userid")
    private Integer userid;

    @TableField("activityName")
    private String activityName;

    @TableField("activityId")
    private Integer activityId;

    @TableField("hours")
    private Float hours;

    @TableField("comment")
    private String comment;

    @TableField("spentOn")
    private Date spentOn;

    @TableField("createdOn")
    private Date createdOn;

    @TableField("updatedOn")
    private Date updatedOn;


}

