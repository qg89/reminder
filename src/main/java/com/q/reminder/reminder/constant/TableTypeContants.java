package com.q.reminder.reminder.constant;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.constant.TableTypeContants
 * @Description :
 * @date :  2023.01.18 13:28
 */
public interface TableTypeContants {

    // 项目工时
    String PROJECT_TIME = "1";
    // 需求管理表
    String FEATURE = "2";
    // 需求管理表临时表
    String FEATURE_TMP = "3";
    // 需求管理表人员配置表
    String FEATURE_USER_CONFIG = "4";

    public interface ViewType {
        // 全部
        String ALL = "0";
        // 上月
        String LAST_MONTH = "1";
        // 昨天
        String YESTDAY = "2";
        // 本月
        String THIS_MONTH = "3";
    }

}
