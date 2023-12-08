package com.q.reminder.reminder.constant;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.constant.FeishuMessageConnstant
 * @Description :
 * @date :  2023.10.30 14:00
 */
public interface FeishuMessageConnstant {
    final String REMINDER_QUEUE_NAME = "feature_queue";
    final String REMINDER_FIELD_CHANGE_QUEUE_NAME = "field_change_queue";
    // 交换机名
    final String REMINDER_EXCHANGE = "feishu_exchange";

    final String REMINDER_FEATURE_ROUTINGKEY = "feishu_change";
}
