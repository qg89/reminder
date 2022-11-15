package com.q.reminder.reminder.vo;

import com.taskadapter.redmineapi.bean.Issue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.WeeklyProjectVo
 * @Description :
 * @date :  2022.11.01 14:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WeeklyProjectVo extends BaseFeishuVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8912201228540480304L;
    private String pmKey;
    private String folderToken;
    private String projectShortName;
    private String redmineUrl;
    private String fileName;
    private String fileToken;
    private String pmOu;
    private String pKey;

    private String blockId;
    private String imageToken;
    private String weeklyReportUrl;
    private List<Issue> allBugList;
}
