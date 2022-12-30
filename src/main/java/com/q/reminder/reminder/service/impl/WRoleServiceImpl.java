package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.WRole;
import com.q.reminder.reminder.mapper.WRoleMapping;
import com.q.reminder.reminder.service.WRoleService;
import com.q.reminder.reminder.util.RoleInvolvementUtils;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色(WRole)表服务实现类
 *
 * @author makejava
 * @since 2022-12-29 09:16:15
 */
@Service
public class WRoleServiceImpl extends ServiceImpl<WRoleMapping, WRole> implements WRoleService {

    @Override
    public List<RoleInvolvementVo> roleInvolvement(WorkloadParamsVo params) {
        List<RoleInvolvementVo> voList = baseMapper.roleInvolvement(params);
        return RoleInvolvementUtils.getRoleInvolvementVos(voList);
    }
}

