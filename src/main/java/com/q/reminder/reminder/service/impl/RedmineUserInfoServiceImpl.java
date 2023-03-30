package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.mapper.RedmineUserInfoMapping;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.util.RoleInvolvementUtils;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Override
    @Cacheable(cacheNames = RedisKeyContents.REDMINE_USERINFO_REDMINE_TYPE, key = "#redmineType")
    public List<RedmineUserInfo> listUsers(String redmineType) {
        LambdaQueryWrapper<RedmineUserInfo> lq = Wrappers.lambdaQuery();
        lq.eq(RedmineUserInfo::getRedmineType, redmineType);
        return baseMapper.selectList(lq);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyContents.REDMINE_USERINFO_REDMINE_ALL, key = "'userAll'")
    public List<RedmineUserInfo> listUserAll() {
        return list(Wrappers.<RedmineUserInfo>lambdaQuery().isNotNull(RedmineUserInfo::getAssigneeName));
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyContents.REDMINE_USERINFO_REDMINE_ALL, allEntries = true)
    public void saveOrupdateMultiIdAll(List<RedmineUserInfo> data) {
        saveOrUpdateBatchByMultiId(data);
    }
}
