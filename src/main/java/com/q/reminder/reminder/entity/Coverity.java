package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.Coverity
 * @Description :
 * @date :  2022.12.01 10:30
 */
@Data
public class Coverity implements Serializable {


    @Serial
    private static final long serialVersionUID = -6554845821348447732L;

    @TableId(type = IdType.INPUT)
    private String id;

    @TableField(value = "c_project_id")
    private String cProjectId;

    @TableField(value = "c_view_id")
    private String cViewId;

    @TableField(value = "r_project_id")
    private String rProjectId;

    @TableField(value = "r_project_name")
    private String rProjectName;

    @TableField(value = "r_assignee_id")
    private String assigneeId;

    @TableField(value = "r_proent_id")
    private String rPproentId;

    @TableField(value = "create_task")
    private String createTask;

    @TableField(value = "is_delete")
    @TableLogic(value = "0", delval = "1")
    private String isDelete;


}
