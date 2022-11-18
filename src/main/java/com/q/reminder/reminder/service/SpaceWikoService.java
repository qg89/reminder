package com.q.reminder.reminder.service;

import com.lark.oapi.Client;
import com.q.reminder.reminder.config.FeishuClient;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.SpaceWikoService
 * @Description :
 * @date :  2022.11.18 14:35
 */
public interface SpaceWikoService {
    void syncSpacesWiki(Client client, String projectToken, String title) throws Exception;
}
