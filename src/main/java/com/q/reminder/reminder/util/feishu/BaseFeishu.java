package com.q.reminder.reminder.util.feishu;

import com.lark.oapi.Client;
import com.q.reminder.reminder.config.SpringContextUtils;
import com.q.reminder.reminder.service.impl.FeishuService;
import com.q.reminder.reminder.util.feishu.cloud.Cloud;
import com.q.reminder.reminder.util.feishu.cloud.documents.Upload;
import com.q.reminder.reminder.util.feishu.group.GroupMessage;
import com.q.reminder.reminder.util.feishu.message.Message;
import com.q.reminder.reminder.util.feishu.wiki.Wiki;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.base.FeishuUtils
 * @Description :
 * @date :  2023.02.23 11:32
 */
public abstract class BaseFeishu {
    protected Client CLIENT;
    protected String TENANT_ACCESS_TOKEN;

    protected BaseFeishu() {
        FeishuService feishuService = SpringContextUtils.getBean(FeishuService.class);
        CLIENT = feishuService.client();
        TENANT_ACCESS_TOKEN = feishuService.tenantAccessToken();
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

    public static Upload upload() {
        return Upload.getInstance();
    }
}
