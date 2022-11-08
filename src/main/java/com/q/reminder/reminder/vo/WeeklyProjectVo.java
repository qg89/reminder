package com.q.reminder.reminder.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

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
    private String accessKey;
    private String folderToken;
    private String projectShortName;
    private String redmineUrl;
    private String fileName;
    private String fileToken;
    private String pKey;

    private String blockId;
}
