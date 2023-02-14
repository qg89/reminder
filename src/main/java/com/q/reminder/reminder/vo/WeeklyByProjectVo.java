package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.WeeklyByProjectVo
 * @Description :
 * @date :  2022.11.15 14:22
 */
@Data
public class WeeklyByProjectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 6292114269540492757L;
    private String fileName;
    private String weeklyReportUrl;
    private Integer weekNum;
    private String id;
}
