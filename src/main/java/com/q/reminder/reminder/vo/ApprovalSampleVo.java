package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ApprovalSampleVo
 * @Description :
 * @date :  2023.02.23 09:51
 */

@Data
public class ApprovalSampleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3237360727941400710L;

    private String approvalCode;
    private String startTime;
    private String endTime;
}
