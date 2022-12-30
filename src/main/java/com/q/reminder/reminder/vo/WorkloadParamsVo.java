package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.WorkloadParamsVo
 * @Description :
 * @date :  2022.12.30 13:45
 */
@Data
public class WorkloadParamsVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -289390047236702876L;

    private String pKey;
    private String year;
    private String groupId;
    private String roleId;
    private String userId;
}
