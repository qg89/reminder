package com.q.reminder.reminder.util.feishu;

import com.lark.oapi.Client;
import com.q.reminder.reminder.util.feishu.cloud.documents.Documents;
import com.q.reminder.reminder.util.feishu.cloud.space.Space;
import com.q.reminder.reminder.util.feishu.cloud.table.Table;
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
    protected BaseFeishu(){}

    protected static Client CLIENT = null;

    /**
     * 云文档-云空间
     * @param client
     * @return
     */
    public static Space cloud(Client client) {
        if (CLIENT == null) {
            CLIENT = client;
        }
        return Space.getInstance();
    }

    /**
     * 云文档-文档
     * @param client
     * @return
     */
    public static Documents block(Client client) {
        if (CLIENT == null) {
            CLIENT = client;
        }
        return Documents.getInstance();
    }

    /**
     * 消息
     * @param client
     * @return
     */
    public static Message message(Client client) {
        if (CLIENT == null) {
            CLIENT = client;
        }
        return Message.getInstance();
    }

    /**
     * 知识空间
     * @param client
     * @return
     */
    public static Wiki wiki(Client client) {
        if (CLIENT == null) {
            CLIENT = client;
        }
        return Wiki.getInstance();
    }

    /**
     * 群组
     * @param client
     * @return
     */
    public static GroupMessage groupMessage(Client client) {
        if (CLIENT == null) {
            CLIENT = client;
        }
        return GroupMessage.getInstance();
    }

    /**
     * 云文档-多维表格
     * @param client
     * @return
     */
    public static Table table(Client client) {
        if (CLIENT == null) {
            CLIENT = client;
        }
        return Table.getInstance();
    }
}
