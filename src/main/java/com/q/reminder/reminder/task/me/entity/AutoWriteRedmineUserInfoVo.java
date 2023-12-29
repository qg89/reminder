package com.q.reminder.reminder.task.me.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.entity.UserInfoVo
 * @Description :
 * @date :  2023.12.29 09:25
 */
@Data
public class AutoWriteRedmineUserInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3104815698432600224L;
    private Integer projectId;
    private Integer issueId;
    private String pmKey;
    private String spentOn;
    private String name;
}
