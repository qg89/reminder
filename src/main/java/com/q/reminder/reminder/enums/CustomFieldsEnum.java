package com.q.reminder.reminder.enums;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.enums.CustomFieldsEnum
 * @Description :
 * @date :  2022.11.14 10:04
 */
public enum CustomFieldsEnum {
    /**
     * Bug影响版本
     */
    BUG_VERSIONS(65, "Bug影响版本" ),
    SOURCE_PROJCET(195, "源项目"),
    BUG_LEVEL(67, "Bug等级"),
    SOURCE_BUG_ID(196, "源BugID"),
    BUG_SOURCE(68, "Bug来源"),
    FEATURE_ID(5, "需求ID"),
    FEATURE_TYPE(42, "需求类型"),
    HORIZONTAL_TAG(5, "是否需要横展标识"),
    TEST_TYPE(69, "测试类型"),
    BUG_Mark(133, "Bug再现标识"),
    BUG_TYPE(72, "Bug类型"),
    CLOSED_STAFF(201, "Closed人员"),
    SECONDARY_BUG_MARKING(74, "二次Bug标识"),
    SECONDARY_BUG_TYPE(75, "二次Bug类型"),
    BUG_VALIDATION_VERSION(75, "Bug验证版本"),
    SOLUTIONS(78, "解决对策"),
    BUG_FIXER(79, "Bug修改人"),
    BUG_CAUSE_CATEGORIES(81, "Bug原因分类"),
    BUG_DESCRIPTION(82, "Bug描述需修改"),
    CASE_ID(215, "用例ID"),
    RELEASED_DATE(220, "Released日期"),
    TASK_PROPERTIES(221, "任务属性"),
    DEV_GROUP(31, "开发组"),
    REMARKS_2(173, "备注2"),
    BUG_FIXING_GROUP(122, "Bug修改组"),
    FUNCTIONAL_CLASSIFICATION(186, "功能分类"),
    RESOLVED_DATE(123, "Resolved日期"),
    CLOSED_DATE(124, "Closed日期"),
    RECURRENCE_FREQUENCY(62, "再现频率"),
    REMARKS_1(62, "备注1"),
    REQUIRE_VALIDATION(30, "是否需要验证"),
    ;


    private Integer id;
    private String name;

    CustomFieldsEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
