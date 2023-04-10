package com.q.reminder.reminder.task.base;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.PublicHolidays;
import com.q.reminder.reminder.service.PublicHolidaysService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.base.HoldayBase
 * @Description :
 * @date :  2022.10.24 09:33
 */
@Component
@RequiredArgsConstructor
public class HoldayBase {
    private final PublicHolidaysService publicHolidaysService;

    /**
     * 查询工作日 是否发送
     * @return
     */
    public Boolean queryHoliday() {
        LambdaQueryWrapper<PublicHolidays> holdayWrapper = new LambdaQueryWrapper<>();
        holdayWrapper.eq(PublicHolidays::getHoliday, DateUtil.today());
        PublicHolidays holidayInfo = publicHolidaysService.getOne(holdayWrapper);
        boolean weekend = DateUtil.isWeekend(new Date());
        if (holidayInfo != null && Objects.equals("0", holidayInfo.getType()) || (holidayInfo == null && weekend)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
