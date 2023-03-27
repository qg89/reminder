package com.q.reminder.reminder.strategys.anno;

import com.q.reminder.reminder.enums.ReminderTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.anno.RedmineTypeAnnotation
 * @Description :
 * @date :  2023.03.27 10:32
 */
@Target({ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RedmineTypeAnnotation {


    ReminderTypeEnum type();
}
