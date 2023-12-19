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
 * (GitConf)实体类
 *
 * @author makejava
 * @since 2023-12-19 17:42:16
 */
@Data
@TableName("git_conf")
public class GitConf extends Model<GitConf> implements Serializable {
    @Serial
    private static final long serialVersionUID = 109562954294033866L;
    
    
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Integer id;
    

     
    @TableField(value = "remote_repo_path")
    private String remoteRepoPath;
     
     
    @TableField(value = "key_path")
    private String keyPath;
     
     
    @TableField(value = "local_path")
    private String localPath;

    @TableField(value = "branch_main")
    private String branchMain;

}

