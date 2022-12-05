package com.q.reminder.reminder.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.Coverity
 * @Description :
 * @date :  2022.10.09 09:15
 */
@Data
@TableName("r_coverity_log")
public class CoverityLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 4135042126732197174L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("c_id")
    private Integer cId;
    /**
     * 类别
     */
    @TableField("display_category")
    private String displayCategory;
    /**
     * 类型
     */
    @TableField("display_type")
    private String displayType;
    /**
     * 文件
     */
    @TableField("display_file")
    private String displayFile;
    /**
     * 代码行数
     */
    @TableField("line_number")
    private String lineNumber;
    /**
     * 组件
     */
    @TableField("display_component")
    private String displayComponent;

    /**
     * 首次被检测时间
     */
    @TableField(exist = false)
    private String firstDetected;

    @TableField("first_dete")
    private Date firstDate;
    /**
     * 影响
     */
    @TableField("displayImpact")
    private String displayImpact;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    @TableField("week_num")
    private Integer weekNum;

    @TableField("assignee_id")
    private String assigneeId;

    @TableField("expired_type")
    private String expiredType;


}
