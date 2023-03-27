package com.q.reminder.reminder.enums;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.enums.ReminderTypeEnum
 * @Description :
 * @date :  2023.03.27 10:33
 */
public enum ReminderTypeEnum {

    OLD(1, "旧redmine"),
    NEW(2, "新redmine"),
    ;

    private Integer type;
    private String value;

    ReminderTypeEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public static ReminderTypeEnum getEnum(Integer orderType) {
        ReminderTypeEnum[] typeArray = ReminderTypeEnum.values();
        for (ReminderTypeEnum typeEnum : typeArray) {
            if (typeEnum.getType().equals(orderType)) {
                return typeEnum;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
