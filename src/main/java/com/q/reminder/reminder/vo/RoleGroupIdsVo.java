package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.RoleGroupIdsVo
 * @Description :
 * @date :  2023.01.03 16:59
 */
@Data
public class RoleGroupIdsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7691019239453074154L;
    private String groupId;
    private String roleId;
    private String roleName;
}
