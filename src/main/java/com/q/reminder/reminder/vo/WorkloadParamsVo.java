package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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

    private List<String> ids;
    private String year;
    private List<String> groupId;
    private List<String> roleId;
    private List<String> userId;
}
