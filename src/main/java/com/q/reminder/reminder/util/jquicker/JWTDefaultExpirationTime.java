package com.q.reminder.reminder.util.jquicker;

import lombok.val;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * JQuicker默认持续时间枚举类,单位秒
 */
public enum JWTDefaultExpirationTime {
    //默认为7天：7*24*60
    DEFAULT_EXPIRATION_TIME(604800),
    WEEK(604800),
    DAY(86400),
    HALF_MONTH(1296000),
    MONTH(2592000);


    private int value;

    JWTDefaultExpirationTime(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static String getFeatureTimeStamp(JWTDefaultExpirationTime time) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, time.getValue());
        return formatCalendar(instance);
    }

    public static String getFeatureTimeStamp(Number time,TimeUnit timeUnit) {
        Calendar instance = Calendar.getInstance();
        instance.add(timeUnit.CorrespondingValue, time.intValue());
        return formatCalendar(instance);
    }

    public static String getTokenExpirationTimeStamp(Number milliSecond){
        Date date = new Date(milliSecond.longValue());
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return formatCalendar(instance);
    }

    private static String formatCalendar(Calendar instance) {
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DAY_OF_MONTH);
        int hour = instance.get(Calendar.HOUR);
        int min = instance.get(Calendar.MINUTE);
        int sec = instance.get(Calendar.SECOND);
        LocalDateTime of = LocalDateTime.of(year, month, day, hour, min, sec);
        String formatDate = of.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String formatTime = of.format(DateTimeFormatter.ISO_LOCAL_TIME);
        return formatDate + " " + formatTime;
    }

    public static Number formatTime(long millisecond, TimeUnit timeUnit){
        if (timeUnit==TimeUnit.DAY){
            millisecond = millisecond / 1000/60/60/24;
        }else if (timeUnit==TimeUnit.HOUR){
            millisecond = millisecond / 1000/60/60;
        }else if (timeUnit==TimeUnit.MILL_SECOND){
            millisecond = millisecond / 1000/60;
        }else if (timeUnit==TimeUnit.SECOND){
            millisecond = millisecond / 1000;
        }
        return millisecond;
    }

    public  enum TimeUnit{
        //天
        DAY("days",5),
        //时
        HOUR("h",10),
        // 分
        MINUTE("min",12),
        // 秒
        SECOND("s",13),
        //毫秒
        MILL_SECOND("ms",14);

        private String unit;
        //数值对应Calendar类
        private int CorrespondingValue;

        TimeUnit() {
        }

        TimeUnit(String unit, int correspondingValue) {
            this.unit = unit;
            CorrespondingValue = correspondingValue;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getCorrespondingValue() {
            return CorrespondingValue;
        }

        public void setCorrespondingValue(int correspondingValue) {
            CorrespondingValue = correspondingValue;
        }
    }
}
