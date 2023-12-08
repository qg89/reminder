package com.q.reminder.reminder.message;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.constant.FeishuMessageConnstant;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.message.FeiShuConsumer
 * @Description :
 * @date :  2023.10.30 13:59
 */
@Component
public class FeiShuConsumer {
    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = FeishuMessageConnstant.REMINDER_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(value = FeishuMessageConnstant.REMINDER_EXCHANGE, type = ExchangeTypes.DIRECT), key = FeishuMessageConnstant.REMINDER_FEATURE_ROUTINGKEY)})
    @RabbitHandler
    public void processFeatureMsg(String records) {
        JSONArray json = JSONArray.from(records);
        for (Object field : json) {

            JSONObject afterJson = JSONObject.from(field);
            String fieldId = afterJson.getString("field_id");
//            String fieldValue = getFieldValue(afterJson.getString("field_value"));
//            if (optionMap.containsKey(fieldValue)) {
//                fieldValue = optionMap.get(fieldValue);
//            }
//            if (projectMap.containsKey(fieldValue)) {
//                table.setProjectKey(projectMap.get(fieldValue));
//            }
//            TableFeatureRole role = new TableFeatureRole();
//            role.setRecordId(recordId);
//            switch (fieldId) {
//                case "fldzBqWKKF" -> table.setModule(fieldValue);
//                case "fldyxlzDlx" -> table.setMenuOne(fieldValue);
//                case "fldVitCKLL" -> table.setMenuTwo(fieldValue);
//                case "fldwfsE7FK" -> table.setMenuThree(fieldValue);
//                case "fldSgKd6Rp" -> table.setDscrptn(fieldValue);
//                case "fldYrJP7Hd" -> table.setFeatureType(fieldValue);
//                case "flds5vtn7k" -> table.setFeatureState(fieldValue);
//                case "fldbO4FXw5" -> table.setFeatureId(fieldValue);
//
//                case "fldM0tclLc" -> role.setRoleType(1);
//                case "fldy8bIsUP" -> role.setRoleType(2);
//                case "fldRwSQfOm" -> role.setRoleType(3);
//                case "fldLuzGYK8" -> role.setRoleType(4);
//                case "fldgPB2P3W" -> role.setRoleType(5);
//            }
//            if (role.getRoleType() != null && StringUtils.isNotBlank(fieldValue)) {
//                role.setRoleTime(fieldValue);
//                roleList.add(role);
//            }
        }
    }

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = FeishuMessageConnstant.REMINDER_FIELD_CHANGE_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(value = FeishuMessageConnstant.REMINDER_EXCHANGE, type = ExchangeTypes.DIRECT), key = FeishuMessageConnstant.REMINDER_FEATURE_ROUTINGKEY)})
    @RabbitHandler
    public void processFieldChangeMsg(String options) {
        JSONObject json = JSONObject.from(options);
        System.out.println("feature_change" + options);
    }
}
