package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.OvertimeVo
 * @Description :
 * @date :  2023/7/4 17:10
 */
@Data
public class OvertimeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2484960588166021552L;

    private Double addWork;
    private Date day;
    private String projectId;
    private String userId;
}
