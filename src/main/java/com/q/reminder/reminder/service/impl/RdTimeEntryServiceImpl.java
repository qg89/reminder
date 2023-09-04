package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.entity.SHolidayConfig;
import com.q.reminder.reminder.mapper.RdTimeEntryMapping;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.service.SHolidayConfigService;
import com.q.reminder.reminder.vo.*;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import com.q.reminder.reminder.vo.params.UserInfoParamsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * (RdTimeEntry)表服务实现类
 *
 * @author makejava
 * @since 2023-01-19 11:47:40
 */
@Service
public class RdTimeEntryServiceImpl extends MppServiceImpl<RdTimeEntryMapping, RdTimeEntry> implements RdTimeEntryService {
    @Autowired
    private SHolidayConfigService sHolidayConfigService;

    @Override
    public List<OvertimeVo> listOvertime(ProjectParamsVo paramsVo) {
        return baseMapper.listOvertime(paramsVo);
    }

    @Override
    public IPage<UserInfoWrokVo> userinfoList(Page<UserInfoWrokVo> page, UserInfoParamsVo vo) {
        IPage<UserInfoWrokVo> wrokVoIPage = baseMapper.userinfoList(page, vo);
        String startTime = vo.getStartTime();
        String endTime = vo.getEndTime();
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            LambdaQueryWrapper<SHolidayConfig> wrapper = Wrappers.<SHolidayConfig>lambdaQuery();
            wrapper.eq(SHolidayConfig::getWork, "1");
            wrapper.between(SHolidayConfig::getDate, startTime, endTime);
            List<SHolidayConfig> list = sHolidayConfigService.list(wrapper);
            int size = list.size() * 8;
            List<UserInfoWrokVo> records = wrokVoIPage.getRecords();
            for (UserInfoWrokVo record : records) {
                Double totalTime = record.getTotalTime();
                record.setEmployeeLoad(totalTime / size);
            }
            wrokVoIPage.setRecords(records);
            return wrokVoIPage;
        }
        return wrokVoIPage;
    }

    @Override
    public IPage<UserInfoTimeVo> userTimeList(Page<UserInfoTimeVo> page, UserInfoParamsVo vo) {
        return baseMapper.userTimeList(page, vo);
    }

    @Override
    public List<OptionVo> userOption() {
        return baseMapper.userOption();
    }

    @Override
    public List<RdTimeEntry> listByProject(ProjectParamsVo vo) {
        return baseMapper.listByProject(vo);
    }

    @Override
    public List<ProjectCostVo> listBySpentOnToCost(ProjectParamsVo param) {
        return baseMapper.listBySpentOnToCost(param);
    }
}
