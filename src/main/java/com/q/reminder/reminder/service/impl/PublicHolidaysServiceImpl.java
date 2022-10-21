package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.PublicHolidays;
import com.q.reminder.reminder.mapper.PublicHolidaysMapping;
import com.q.reminder.reminder.service.PublicHolidaysService;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.PublicHolidaysServiceImpl
 * @Description :
 * @date :  2022.10.21 15:18
 */
@Service
public class PublicHolidaysServiceImpl extends ServiceImpl<PublicHolidaysMapping, PublicHolidays>implements PublicHolidaysService {
}
