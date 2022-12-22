package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ProjectReaVo
 * @Description :
 * @date :  2022.12.21 19:16
 */
@Data
public class ProjectReaVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7559515469488244195L;
    private String pId;
    private String chatId;
    private String userId;
    private String cProjectId;
}
