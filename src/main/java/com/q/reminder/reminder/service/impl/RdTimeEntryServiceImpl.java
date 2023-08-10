package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.mapper.RdTimeEntryMapping;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.vo.*;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import com.q.reminder.reminder.vo.params.UserInfoParamsVo;
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

    @Override
    public List<OvertimeVo> listOvertime(ProjectParamsVo paramsVo) {
        return baseMapper.listOvertime(paramsVo);
    }

    @Override
    public IPage<UserInfoWrokVo> userinfoList(Page<UserInfoWrokVo> page, UserInfoParamsVo vo) {
        return baseMapper.userinfoList(page, vo);
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
    public List<ProjectCostVo> listProjectByDate(ProjectParamsVo param) {
        return baseMapper.listByProjectByDate(param);
    }
}
