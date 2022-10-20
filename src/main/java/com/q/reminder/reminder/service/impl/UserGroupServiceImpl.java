package com.q.reminder.reminder.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.UserGroup;
import com.q.reminder.reminder.mapper.UserGroupMapping;
import com.q.reminder.reminder.service.UserGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.UserGroupServiceImpl
 * @Description :
 * @date :  2022.09.27 20:16
 */
@Service
public class UserGroupServiceImpl extends MppServiceImpl<UserGroupMapping, UserGroup> implements UserGroupService {
    @Override
    public Boolean saveBatchAll(List<UserGroup> userGroupList) {
        return baseMapper.saveBatchAll(userGroupList);
    }
}
