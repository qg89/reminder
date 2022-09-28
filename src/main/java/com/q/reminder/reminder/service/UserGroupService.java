package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.UserGroup;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.UserGroupService
 * @Description :
 * @date :  2022.09.27 20:15
 */
public interface UserGroupService extends IService<UserGroup> {
    Boolean saveBatchAll(List<UserGroup> userGroupList);
}
