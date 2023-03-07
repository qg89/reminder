package com.q.reminder.reminder.config;

import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Tracker;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.config.RedmineConfig
 * @Description :
 * @date :  2023.03.07 16:58
 */
public class RedmineConfig {
    public static Tracker FEATURE_TRACKER = new Tracker().setId(2).setName("需求");
    public static Tracker DEV_TRACKER;
    public static Tracker TEST_TRACKER;

    public static List<CustomField> CUSTOM_FIELDS;

    private static CustomField CUSTOM_FIELD = new CustomField();

    public static RedmineConfig type(String type) {
        if ("1".equals(type)) {
            oldRedmine();
        }
        if ("2".equals(type)) {
            newRedmine();
        }
        return new RedmineConfig();
    }

    private static void newRedmine() {
        DEV_TRACKER = new Tracker().setId(3).setName("研发");
        TEST_TRACKER = new Tracker().setId(4).setName("测试");

        CUSTOM_FIELDS = List.of(
                new CustomField()
                        .setId(286)
                        .setName("需求类型")
                        .setValue("功能")
        );
        CUSTOM_FIELD = new CustomField()
                .setId(226)
                .setName("需求ID")
        ;
    }

    private static void oldRedmine() {
        DEV_TRACKER = new Tracker().setId(7).setName("开发");
        TEST_TRACKER = new Tracker().setId(8).setName("测试");
        CUSTOM_FIELDS = List.of(
                new CustomField()
                        .setId(42)
                        .setName("需求类型")
                        .setValue("功能"),
                new CustomField()
                        .setId(30)
                        .setName("是否需要验证")
                        .setValue("是")
        );
        CUSTOM_FIELD = new CustomField()
                .setId(5)
                .setName("需求ID")
        ;
    }

    public CustomField setCustomValue(String value) {
        return CUSTOM_FIELD.setValue(value);
    }
}
