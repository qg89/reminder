package com.q.reminder.reminder.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * (RdIssue)实体类
 *
 * @author makejava
 * @since 2023-01-30 11:25:03
 */
@Data
@TableName("rd_issue")
public class RdIssue extends Model<RdIssue> implements Serializable {
    @Serial
    private static final long serialVersionUID = -32900323430671511L;
    
        
    @MppMultiId
    private Integer id;
        
    @MppMultiId
    private Integer projectid;
        
    private String subject;
        
    private String projectname;
        
    private Date startdate;
        
    private Date duedate;
        
    private Date createdon;
        
    private Date updatedon;
        
    private Integer doneratio;
        
    private Integer parentid;
        
    private Integer priorityid;
        
    private String prioritytext;
        
    private Double estimatedhours;
        
    private Double spenthours;
        
    private Integer assigneeid;
        
    private String assigneename;
        
    private String notes;
        
    private Integer authorid;
        
    private String authorname;
        
    private String description;
        
    private Date closedon;
        
    private Integer statusid;
        
    private String statusname;
        
    private String privateissue;


}

