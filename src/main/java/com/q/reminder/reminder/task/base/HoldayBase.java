package com.q.reminder.reminder.task.base;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.entity.PublicHolidays;
import com.q.reminder.reminder.service.PublicHolidaysService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

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
        PublicHolidays holidayInfo = Optional.ofNullable(publicHolidaysService.getOne(holdayWrapper)).orElse(new PublicHolidays());
        String holidayType = holidayInfo.getType();
        boolean weekend = DateUtil.isWeekend(new Date());
        if (Objects.equals("0", holidayType) || (weekend && !Objects.equals("1", holidayType))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
