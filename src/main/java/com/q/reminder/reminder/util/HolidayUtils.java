package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.nlf.calendar.Holiday;
import com.nlf.calendar.util.HolidayUtil;
import org.joda.time.DateTime;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.HolidayUtils
 * @Description :
 * @date :  2023/6/18 16:34
 */
public abstract class HolidayUtils {

    public static boolean isHoliday() {
        DateTime now = new DateTime();
        Holiday holiday = HolidayUtil.getHoliday(now.toString("yyyy-MM-dd"));
        return (holiday == null && DateUtil.isWeekend(now.toDate())) || (holiday != null && !holiday.isWork());
    }
}
