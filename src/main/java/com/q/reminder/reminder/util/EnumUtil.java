package com.q.reminder.reminder.util;


import com.q.reminder.reminder.enums.CodeEnum;

public abstract class EnumUtil {

    /**
     * 枚举公共工具类，通过枚举code，查找返回枚举类
     * 枚举类需要 implements CodeEnum<T></>
     *
     * @param enumClass 需要返回的枚举类
     * @param code      枚举code
     * @return 返回的枚举类
     */
    public static <T extends CodeEnum<String>> T getByCode(Class<T> enumClass, String code) {
        for (T each : enumClass.getEnumConstants()) {
            if (each.getCode().equals(code)) {
                return each;
            }
        }
        return null;
    }

    /**
     * 枚举公共工具类，通过枚举code，查找返回枚举类
     * 枚举类需要 implements CodeEnum<T></>
     *
     * @param enumClass 需要返回的枚举类
     * @param code      枚举code
     * @return 返回的枚举类
     */
    public static <T extends CodeEnum<Integer>> T getByCode(Class<T> enumClass, Integer code) {
        for (T each : enumClass.getEnumConstants()) {
            if (each.getCode().equals(code)) {
                return each;
            }
        }
        return null;
    }
}
