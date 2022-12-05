package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.CoverityLog;
import com.q.reminder.reminder.mapper.CoverityLogMapping;
import com.q.reminder.reminder.service.CoverityLogService;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.CoverityLogServiceImpl
 * @Description :
 * @date :  2022.12.02 11:54
 */
@Service
public class CoverityLogServiceImpl extends ServiceImpl<CoverityLogMapping, CoverityLog> implements CoverityLogService {
}
