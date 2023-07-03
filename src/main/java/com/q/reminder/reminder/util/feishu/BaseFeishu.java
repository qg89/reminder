package com.q.reminder.reminder.util.feishu;

import com.lark.oapi.Client;
import com.q.reminder.reminder.util.SpringContextUtils;
import com.q.reminder.reminder.service.impl.FeishuService;
import com.q.reminder.reminder.util.feishu.cloud.Cloud;
import com.q.reminder.reminder.util.feishu.group.GroupMessage;
import com.q.reminder.reminder.util.feishu.message.Message;
import com.q.reminder.reminder.util.feishu.wiki.Wiki;
import lombok.extern.log4j.Log4j2;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.base.FeishuUtils
 * @Description :
 * @date :  2023.02.23 11:32
 */
@Log4j2
public abstract class BaseFeishu {
    private FeishuService feishuService = SpringContextUtils.getBean("feishuService", FeishuService.class);
    protected Client CLIENT = feishuService.client();
//    protected RequestOptions REQUEST_OPTIONS = RequestOptions.newBuilder().tenantAccessToken(feishuService.tenantAccessToken()).build();

    protected BaseFeishu() {
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
