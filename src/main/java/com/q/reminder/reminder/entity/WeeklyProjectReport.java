package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.WeeklyProjectReport
 * @Description :
 * @date :  2022.11.01 09:22
 */
@Data
@TableName("fs_weekly_project_report")
public class WeeklyProjectReport implements Serializable {

    @Serial
    private static final long serialVersionUID = -2887572064638029182L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("file_token")
    private String fileToken;

    @TableField("folder_token")
    private String folderToken;

    @TableField("week_num")
    private String weekNnum;

    @TableField("year_num")
    private String yearNum;

    @TableField("file_name")
    private String fileName;

    @TableField("project_short_name")
    private String projectShortName;

    @TableField("r_pid")
    private Integer rPid;

    @TableLogic(value = "0", delval = "1")
    @TableField("is_delete")
    private String isDelete;

    @TableField("update_time")
    private String updateTime;

    @TableField("create_time")
    private String createTime;

}
