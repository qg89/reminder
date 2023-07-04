package com.q.reminder.reminder.service.impl;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.mapper.RdTimeEntryMapping;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.vo.OvertimeVo;
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
    public List<OvertimeVo> listOvertime(String ym) {
        return baseMapper.listOvertime(ym);
    }
}
