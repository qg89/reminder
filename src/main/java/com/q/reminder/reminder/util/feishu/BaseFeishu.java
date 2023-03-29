package com.q.reminder.reminder.util.feishu;

import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.q.reminder.reminder.config.SpringContextUtils;
import com.q.reminder.reminder.service.impl.FeishuService;
import com.q.reminder.reminder.util.feishu.cloud.Cloud;
import com.q.reminder.reminder.util.feishu.group.GroupMessage;
import com.q.reminder.reminder.util.feishu.message.Message;
import com.q.reminder.reminder.util.feishu.wiki.Wiki;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.base.FeishuUtils
 * @Description :
 * @date :  2023.02.23 11:32
 */
public abstract class BaseFeishu {
    protected Client CLIENT;
    protected RequestOptions REQUEST_OPTIONS;

    protected BaseFeishu() {
        FeishuService feishuService = SpringContextUtils.getBean(FeishuService.class);
        CLIENT = feishuService.client();
        String tenantAccessToken;
        if (StringUtils.isBlank(tenantAccessToken = feishuService.tenantAccessToken())) {
            int i = 0;
            while (StringUtils.isBlank(tenantAccessToken) && (i++) <= 5) {
                try {
                    Thread.sleep(1000 * 30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        REQUEST_OPTIONS = RequestOptions.newBuilder().tenantAccessToken(tenantAccessToken).build();
    }

    /**
     * 云文档-云空间
     *
     * @return
     */
    public static Cloud cloud() {
        return Cloud.getInstance();
    }

    /**
     * 消息
     *
     * @return
     */
    public static Message message() {
        return Message.getInstance();
    }

    /**
     * 知识空间
     *
     * @return
     */
    public static Wiki wiki() {
        return Wiki.getInstance();
    }

    /**
     * 群组
     *
     * @return
     */
    public static GroupMessage groupMessage() {
        return GroupMessage.getInstance();
    }
}
