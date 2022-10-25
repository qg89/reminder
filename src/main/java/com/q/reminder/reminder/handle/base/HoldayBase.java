package com.q.reminder.reminder.handle.base;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.PublicHolidays;
import com.q.reminder.reminder.service.PublicHolidaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.base.HoldayBase
 * @Description :
 * @date :  2022.10.24 09:33
 */
@Component
public class HoldayBase {
    @Autowired
    private PublicHolidaysService publicHolidaysService;

    /**
     * 查询工作日 是否发送
     * @return
     */
    public Boolean queryHoliday() {
        LambdaQueryWrapper<PublicHolidays> holdayWrapper = new LambdaQueryWrapper<>();
        holdayWrapper.eq(PublicHolidays::getHoliday, DateUtil.today());
        PublicHolidays holidays = publicHolidaysService.getOne(holdayWrapper);
        return DateUtil.isWeekend(new Date()) && (holidays == null || !"1".equals(holidays.getType()));
    }
}
