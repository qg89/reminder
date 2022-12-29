package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.mapper.RedmineUserInfoMapping;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.RoleInvolvementUtils;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
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
public class RedmineUserInfoServiceImpl extends ServiceImpl<RedmineUserInfoMapping, RedmineUserInfo> implements RedmineUserInfoService {
    @Override
    public List<RoleInvolvementVo> roleInvolvement(String pId, String year) {
        List<RoleInvolvementVo> roleInvolvementVos = baseMapper.roleInvolvement(pId, year);
        return RoleInvolvementUtils.getRoleInvolvementVos(roleInvolvementVos);
    }

    @Override
    public List<RoleInvolvementVo> residualWorkload(String pId, String year) {
        List<RoleInvolvementVo> roleInvolvementVos = baseMapper.residualWorkload(pId, year);
        return RoleInvolvementUtils.getRoleInvolvementVos(roleInvolvementVos);
    }
}
