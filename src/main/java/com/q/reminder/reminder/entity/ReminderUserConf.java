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
 * redmine 设置不提醒写日报列表(ReminderUserConf)实体类
 *
 * @author makejava
 * @since 2023-09-06 16:52:29
 */
@Data
@TableName("s_reminder_user_conf")
public class ReminderUserConf extends Model<ReminderUserConf> implements Serializable {
    @Serial
    private static final long serialVersionUID = 138871064272357608L;
    
    
    @TableId(type = IdType.AUTO)
    @TableField(value = "member_id")
    private String memberId;

    @TableField(value = "name")
    private String name;
     
    @TableField(value = "enable")
    private Integer enable;
     
/**
     * 开始日期
     */     
    @TableField(value = "start_date")
    private Date startDate;
     
/**
     * 结束日期
     */     
    @TableField(value = "end_date")
    private Date endDate;
     
}

