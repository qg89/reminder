package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.SDateConfig;
import com.q.reminder.reminder.mapper.SDateConfigMapping;
import com.q.reminder.reminder.service.SDateConfigService;
import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;


/**
 * 日期表(SDateConfig)表服务实现类
 *
 * @author makejava
 * @since 2022-12-28 10:30:49
 */
@Service
public class SDateConfigServiceImpl extends ServiceImpl<SDateConfigMapping, SDateConfig> implements SDateConfigService {
}
