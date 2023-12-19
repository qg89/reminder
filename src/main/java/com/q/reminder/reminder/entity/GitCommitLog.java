package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;



/**
 * (GitCommitLog)实体类
 *
 * @author makejava
 * @since 2023-12-19 17:39:38
 */
@Data
@TableName("git_commit_log")
public class GitCommitLog extends Model<GitCommitLog> implements Serializable {
    @Serial
    private static final long serialVersionUID = -24490759452541313L;
    
    
    @TableId(type = IdType.INPUT)
    @TableField(value = "commit_id")
    private String commitId;

    @TableField(value = "conf_id")
    private Integer confId;
     
    @TableField(value = "branch")
    private String branch;
     
     
    @TableField(value = "is_merge")
    private String isMerge;
     
     
    @TableField(value = "project_cde")
    private String projectCde;
     
     
    @TableField(value = "remove_line")
    private Integer removeLine;
     
     
    @TableField(value = "add_line")
    private Integer addLine;
     
     
    @TableField(value = "commit_message")
    private String commitMessage;
     
     
    @TableField(value = "commit_date")
    private String commitDate;
     
     
    @TableField(value = "auth_name")
    private String authName;
     
     
    @TableField(value = "email_address")
    private String emailAddress;
     
}

