package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.QueryRedmineVo
 * @Description :
 * @date :  2022.10.25 08:57
 */
@Data
public class RedmineVo implements Serializable {

    private static final long serialVersionUID = -3624229091490386847L;
    private String redmineId;
    private String subject;
    private String statusName;
    private String projectName;
    private Date updatedOn;
    private Date dueDate;
    private String assigneeId;
    private String assigneeName;
    private String redmineUrl;
}
