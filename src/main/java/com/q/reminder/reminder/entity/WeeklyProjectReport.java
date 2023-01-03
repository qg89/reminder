package com.q.reminder.reminder.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFilter;
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

    @JSONField(name = "token")
    @TableField("file_token")
    private String fileToken;

    @JSONField(name = "week_num")
    @TableField("week_num")
    private String weekNum;

    @JSONField(name = "name")
    @TableField("file_name")
    private String fileName;

    @JSONField(name = "url")
    @TableField("url")
    private String url;

    @TableField("r_pid")
    private Long rPid;

    @TableLogic(value = "0", delval = "1")
    @TableField("is_delete")
    private String isDelete;

    @TableField("update_time")
    private String updateTime;

    @TableField("create_time")
    private String createTime;

}
