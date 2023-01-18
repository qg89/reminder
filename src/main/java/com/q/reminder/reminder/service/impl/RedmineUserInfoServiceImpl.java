package com.q.reminder.reminder.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.mapper.RedmineUserInfoMapping;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.RoleInvolvementUtils;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.RedmineUserInfoServiceImpl
 * @Description :
 * @date :  2022.10.27 16:20
 */
@Service
public class RedmineUserInfoServiceImpl extends MppServiceImpl<RedmineUserInfoMapping, RedmineUserInfo> implements RedmineUserInfoService {
    @Override
    public List<RoleInvolvementVo> roleInvolvement(WorkloadParamsVo paramsVo) {
        List<RoleInvolvementVo> roleInvolvementVos = baseMapper.roleInvolvement(paramsVo);
        return RoleInvolvementUtils.getRoleInvolvementVos(roleInvolvementVos);
    }

    @Override
    public List<RoleInvolvementVo> residualWorkload(WorkloadParamsVo paramsVo) {
        List<RoleInvolvementVo> roleInvolvementVos = baseMapper.residualWorkload(paramsVo);
        return RoleInvolvementUtils.getRoleInvolvementVos(roleInvolvementVos);
    }

    @Override
    public List<RoleInvolvementVo> groupUserWorkload(WorkloadParamsVo paramsVo) {
        List<RoleInvolvementVo> roleInvolvementVos = baseMapper.groupUserWorkload(paramsVo);
        return RoleInvolvementUtils.getRoleInvolvementVos(roleInvolvementVos);
    }
}
