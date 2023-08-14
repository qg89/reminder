package com.q.reminder.reminder.util;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.q.reminder.reminder.ano.Format;

import java.io.IOException;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.FormateUtils
 * @Description :
 * @date :  2023/8/14 10:50
 */
public class FormatUtils extends JsonSerializer<Number> implements ContextualSerializer {

    private Format format;

    public FormatUtils(Format format) {
        this.format = format;
    }

    public FormatUtils(){}

    @Override
    public void serialize(Number value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value == null) {
            return;
        }
        int scale = format.value();
        jsonGenerator.writeObject(NumberUtil.round(value.doubleValue(), scale).doubleValue());
    }
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        Format annotation = property.getAnnotation(Format.class);
        if (annotation != null) {
            return new FormatUtils(annotation);
        }
        return this;
    }
}
