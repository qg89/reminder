package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.SDateConfig;
import com.q.reminder.reminder.entity.WUserTimeMonth;
import com.q.reminder.reminder.mapper.WUserTimeMonthMapping;
import com.q.reminder.reminder.service.SDateConfigService;
import com.q.reminder.reminder.service.WUserTimeMonthService;
import com.q.reminder.reminder.util.RoleInvolvementUtils;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.UserTimeMonthRatioVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * (WUserTimeMonth)表服务实现类
 *
 * @author makejava
 * @since 2022-12-28 10:32:52
 */
@Service
public class WUserTimeMonthServiceImpl extends ServiceImpl<WUserTimeMonthMapping, WUserTimeMonth> implements WUserTimeMonthService {

    @Autowired
    private SDateConfigService sDateConfigService;
    @Autowired
    private WUserTimeMonthService wUserTimeMonthService;

    @Override
    public List<RoleInvolvementVo> inputRatio(WorkloadParamsVo params) {
        List<RoleInvolvementVo> list = baseMapper.inputRatio(params);
        return RoleInvolvementUtils.getRoleInvolvementVos(list);
    }

    @Override
    public Boolean inputRatioEdit(List<UserTimeMonthRatioVo> list) {
        List<WUserTimeMonth> data = new ArrayList<>();
        list.forEach(vo -> {
            LambdaQueryWrapper<SDateConfig> lq = Wrappers.lambdaQuery();
            lq.eq(SDateConfig::getYear, vo.getYear());
            lq.eq(SDateConfig::getMonth, vo.getMonth());
            SDateConfig dateConfig = sDateConfigService.getOne(lq);
            WUserTimeMonth entity = new WUserTimeMonth();
            entity.setTimes(BigDecimal.valueOf(vo.getRatio()));
            entity.setDateId(dateConfig.getId());
            entity.setUserId(vo.getUserId());
            entity.setPId(vo.getPId());
            data.add(entity);
        });
        return wUserTimeMonthService.saveOrUpdateBatch(data);
    }
}
