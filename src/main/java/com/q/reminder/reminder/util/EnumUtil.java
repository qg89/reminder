package com.q.reminder.reminder.util;


import com.q.reminder.reminder.enums.CodeEnum;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 枚举转map结合value作为map的key,description作为map的value
     *
     * @param enumT
     * @param methodNames
     * @return enum mapcolloction
     */
    public static <T> Map<Object, String> enumToMap(Class<T> enumT, String... methodNames) {
        Map<Object, String> enummap = new HashMap<Object, String>();
        if (!enumT.isEnum()) {
            return enummap;
        }
        T[] enums = enumT.getEnumConstants();
        if (enums == null || enums.length <= 0) {
            return enummap;
        }
        int count = methodNames.length;
        /**默认接口value方法*/
        String valueMathod = "getValue";
        /**默认接口typeName方法*/
        String desMathod = "getTypeName";
        /**扩展方法*/
        if (count >= 1 && !"".equals(methodNames[0])) {
            valueMathod = methodNames[0];
        }
        if (count == 2 && !"".equals(methodNames[1])) {
            desMathod = methodNames[1];
        }
        for (int i = 0, len = enums.length; i < len; i++) {
            T tobj = enums[i];
            try {
                /**获取value值*/
                Object resultValue = getMethodValue(valueMathod, tobj);
                if ("".equals(resultValue)) {
                    continue;
                }
                /**获取typeName描述值*/
                Object resultDes = getMethodValue(desMathod, tobj);
                /**如果描述不存在获取属性值*/
                if ("".equals(resultDes)) {
                    resultDes = tobj;
                }
                enummap.put(resultValue, resultDes + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return enummap;
    }

    /**
     * 根据反射，通过方法名称获取方法值，忽略大小写的
     *
     * @param methodName
     * @param obj
     * @param args
     * @return return value
     */
    private static <T> Object getMethodValue(String methodName, T obj, Object... args) {
        Object resut = "";
        try {
            /********************************* start *****************************************/
            /**获取方法数组，这里只要共有的方法*/
            Method[] methods = obj.getClass().getMethods();
            if (methods.length <= 0) {
                return resut;
            }
            Method method = null;
            for (int i = 0, len = methods.length; i < len; i++) {
                /**忽略大小写取方法*/
                if (methods[i].getName().equalsIgnoreCase(methodName)) {
                    /**如果存在，则取出正确的方法名称*/
                    methodName = methods[i].getName();
                    method = methods[i];
                    break;
                }
            }
            /*************************** end ***********************************************/
            if (method == null) {
                return resut;
            }
            /**方法执行*/
            resut = method.invoke(obj, args);
            if (resut == null) {
                resut = "";
            }
            /**返回结果*/
            return resut;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resut;
    }
}
