package com.q.reminder.reminder.enums;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.enums.ReminderTypeEnum
 * @Description :
 * @date :  2023.03.27 10:33
 */
@Getter
public enum TableFieldEnum {

    FIELD_REDMINE("fldeDXrMOV", "writeType"),
    FIELD_TEST("fldLuzGYK8", "test"),
    FIELD_PROJECT("fldnlI18sF", "prjct"),
    FIELD_FRONT("fldRwSQfOm", "front"),
    FIELD_NAME("fldSgKd6Rp", "dscrptn"),
    FIELD_ONE("fldyxlzDlx", "menuOne"),
    FIELD_TWO("fldVitCKLL", "menuTwo"),
    FIELD_THREE("fldwfsE7FK", "menuThree"),
    FIELD_BACK("fldy8bIsUP", "back"),
    FIELD_FEATURE_TYPE("fldYrJP7Hd", "featureType"),
    FIELD_MDL("fldzBqWKKF", "mdl"),
    FIELD_PROJECT_KEY("prjctKey", "prjctKey"),

    ;
    private String fieldId;

    private String value;

    TableFieldEnum(String fieldId, String value) {
        this.fieldId = fieldId;
        this.value = value;
    }

    public static String getValue(String fieldId) {
        for (TableFieldEnum value : TableFieldEnum.values()) {
            if (value.getFieldId().equals(fieldId)) {
                return value.getValue();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fieldId", fieldId)
                .append("value", value)
                .toString();
    }
}
