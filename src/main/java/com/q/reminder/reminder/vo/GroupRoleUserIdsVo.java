package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.GroupRoleUserIdsVo
 * @Description :
 * @date :  2023.01.03 17:03
 */
@Data
public class GroupRoleUserIdsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8514685714505007164L;
    private String roleId;
    private String roleName;
    private String groupId;
    private String groupName;
    private String userId;
    private String userName;
}
