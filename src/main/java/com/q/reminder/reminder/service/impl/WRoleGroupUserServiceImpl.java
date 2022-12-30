package com.q.reminder.reminder.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.WRoleGroupUser;
import com.q.reminder.reminder.mapper.WRoleGroupUserMapping;
import com.q.reminder.reminder.service.WRoleGroupUserService;
import com.q.reminder.reminder.vo.OptionVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (WRoleGroupUser)表服务实现类
 *
 * @author makejava
 * @since 2022-12-28 10:30:54
 */
@Service
public class WRoleGroupUserServiceImpl extends MppServiceImpl<WRoleGroupUserMapping, WRoleGroupUser> implements WRoleGroupUserService {

    @Override
    public List<OptionVo> option(WorkloadParamsVo paramsVo) {
        String groupId = paramsVo.getGroupId();
        String roleId = paramsVo.getRoleId();
        String userId = paramsVo.getUserId();
        List<OptionVo> list = baseMapper.option();
        if (StringUtils.isNotBlank(groupId)) {
            list = list.stream().filter(e -> groupId.equals(e.getGroupId())).toList();
        }
        if (StringUtils.isNotBlank(roleId)) {
            list = list.stream().filter(e -> roleId.equals(e.getRoleId())).toList();
        }
        if (StringUtils.isNotBlank(userId)) {
            list = list.stream().filter(e -> userId.equals(e.getUserId())).toList();
        }
        return list;
    }
}
