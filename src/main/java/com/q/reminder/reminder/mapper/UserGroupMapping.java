package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.q.reminder.reminder.entity.UserGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.mapper.UserGroupMapping
 * @Description :
 * @date :  2022.09.27 20:14
 */
@Mapper
public interface UserGroupMapping extends MppBaseMapper<UserGroup> {
    Boolean saveBatchAll(List<UserGroup> userGroupList);
}
