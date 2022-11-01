package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.WeeklyProjectVo
 * @Description :
 * @date :  2022.11.01 14:17
 */
@Data
public class WeeklyProjectVo implements Serializable {
    private String appId;
    private String appSecret;
    private String fileToken;
    private String folderToken;
    private String projectSshortName;
}
