package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.WUserTimes;
import com.q.reminder.reminder.mapper.WUserTimesMapping;
import com.q.reminder.reminder.service.WUserTimesService;
import com.q.reminder.reminder.vo.ProjectUserTimeVo;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

/**
 * (WUserTimes)表服务实现类
 *
 * @author makejava
 * @since 2022-12-28 11:32:44
 */
@Service
public class WUserTimesServiceImpl extends ServiceImpl<WUserTimesMapping, WUserTimes> implements WUserTimesService {

    @Override
    public List<Map<String, String>> listByTable(String day, String dayType) {
        return baseMapper.listByTable(day, dayType);
    }
}
