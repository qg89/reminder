package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.OverdueTaskHistory
 * @Description :
 * @date :  2022.09.27 14:31
 */
@TableName("overdue_tasks_history")
@Data
public class OverdueTaskHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = -6088421893531262682L;
    @TableId(type = IdType.INPUT)
    private String redmineId;
    private String projectName;
    private String subjectName;
    private String assigneeName;
    private String type;
    private Date lastUpdateTime;
    private Date createTime;

}
