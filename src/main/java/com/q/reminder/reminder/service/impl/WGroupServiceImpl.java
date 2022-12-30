package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.WGroup;
import com.q.reminder.reminder.mapper.WGroupMapping;
import com.q.reminder.reminder.service.WGroupService;
import com.q.reminder.reminder.util.RoleInvolvementUtils;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.List;

/**
 * (WGroup)表服务实现类
 *
 * @author makejava
 * @since 2022-12-28 10:35:13
 */
@Service
public class WGroupServiceImpl extends ServiceImpl<WGroupMapping, WGroup> implements WGroupService {

    @Override
    public List<RoleInvolvementVo> groupWorkload(WorkloadParamsVo params) {
        List<RoleInvolvementVo> roleInvolvementVos = baseMapper.groupWorkload(params);
        return RoleInvolvementUtils.getRoleInvolvementVos(roleInvolvementVos);
    }
}
