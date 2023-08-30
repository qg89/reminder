package com.q.reminder.reminder;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.nlf.calendar.Holiday;
import com.nlf.calendar.util.HolidayUtil;

import java.util.Date;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.Test
 * @Description :
 * @date :  2023/8/9 16:19
 */
public class Test {

    public static void main(String[] args) {
        DateTime sDate = DateUtil.beginOfMonth(new Date());
        DateTime eDate = DateUtil.date();
        String startTime = sDate.toString("yyy-MM-dd");
        String endTime = eDate.toString("yyy-MM-dd");
        long i = DateUtil.between(sDate, eDate, DateUnit.DAY);
//        System.out.println(startTime);
//        System.out.println(endTime);
//        System.out.println(i);

        //使用随机密钥加密需要加密的数据，列如数据库url,username,password
//        String decrypt = AES.decrypt("YAU7VUQTQlBTP/D7DYdMI5A5u1zqyh85JI/hVpPjrLzCpZnihi+gruSUQTzq8hl9p8mY2czOCrvHMrNNn5hE2869zSbitJStXAKwXQKs6yzl2JhuHSDinw5IyD2v7td4WFtaEQNW8VYwfi1CT17S4/Zf44Yht4Y0Pm/gaRPgSokkAa2s5qe1tu3m1oF4nCFiWsPOwe2hWdR6b6hI5L1Dca0CZCVTVW4dBM+ceuRmoEHR68ILnwSrc+jk7STgvjU+aQ3O/dFxmN7D2qLattxa5wEJjr9Xg5hxczIrC75/SBfst0IOmzqpQdgOyplnL6VGc7KZmcqxKunLwVhdNZqpCQ==", "b9dde9d3d0a82d37");
//        System.out.println(decrypt);

        DateTime dateTime = DateUtil.offsetDay(new Date(), -1);

        // 判断周末
        while (DateUtil.isWeekend(dateTime)) {
            dateTime.offset(DateField.DAY_OF_MONTH, -1);
        }
        String date = dateTime.toString("yyyy-MM-dd");
        Holiday holiday = HolidayUtil.getHoliday(date);
        if (holiday == null || holiday.isWork()) {

        }
        System.out.println(dateTime);
    }
}
