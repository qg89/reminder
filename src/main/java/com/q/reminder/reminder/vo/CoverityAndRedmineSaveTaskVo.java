package com.q.reminder.reminder.vo;

import com.q.reminder.reminder.entity.CoverityLog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.RedmineSaveTaskVo
 * @Description :
 * @date :  2022.10.13 11:42
 */
@Data
public class CoverityAndRedmineSaveTaskVo implements Serializable {
    /**
     * redmine 指派人ID
     */
    private Integer assigneeId;
    /**
     * redmine 项目ID
     */
    private Integer redmineProjectId;
    /**
     * 项目名称
     */
    private String redmineProjectName;
    /**
     * redmine 任务父ID
     */
    private Integer parentId;
    /**
     * redmine备注
     */
    private String description;

    /**
     * redmine 任务主题
     */
    private String subject;

    // ----------------- coverity
    /**
     * coverity 视图ID
     */
    private Integer viewId;
    /**
     * coverity 项目ID
     */
    private Integer coverityProjectId;

    private String redmineUrl;

    private String apiAccessKey;

    private String receiveId;

    private Integer coverityNo;

    private String memberId;

    private String name;

    private List<CoverityLog> coverityLogs;

}
