package com.q.reminder.reminder.ano;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.q.reminder.reminder.util.FormatUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.ano.Formate
 * @Description :
 * @date :  2023/8/14 10:17
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = FormatUtils.class)
public @interface Format {

    int value() default 0;

}
