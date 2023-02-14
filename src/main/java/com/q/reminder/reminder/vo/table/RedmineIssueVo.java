package com.q.reminder.reminder.vo.table;

import com.taskadapter.redmineapi.bean.CustomField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.table.RedmineIssueVo
 * @Description :
 * @date :  2023.01.30 16:07
 */
@Data
public class RedmineIssueVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5831504553953403563L;

    /**
     * 需求ID
     */
    private String featureId;
    /**
     * 跟踪ID
     */
    private String tracker;
    /**
     * 跟踪名称
     */
    private String trackerName;
    /**
     * 指派给
     */
    private Integer assigneeId;
    /**
     * 创建日期
     */
    private Date createdOn;
    /**
     * 计划完成时间
     */
    private Date dueDate;
    /**
     * 主题
     */
    private String subject;
    /**
     * 项目ID
     */
    private String projectId;
    /**
     * 拓展
     */
    private Collection<CustomField> customFields;
    /**
     * 描述
     */
    private String description;
}
