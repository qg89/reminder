package com.q.reminder.reminder.service.impl;

import com.lark.oapi.Client;
import com.q.reminder.reminder.service.SpaceWikoService;
import com.q.reminder.reminder.util.FeishuJavaUtils;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.SpaceWikoServiceImpl
 * @Description :
 * @date :  2022.11.18 14:36
 */
@Service
public class SpaceWikoServiceImpl implements SpaceWikoService {

    @Override
    public void syncSpacesWiki(Client client, String projectToken, String title) throws Exception {
       FeishuJavaUtils.syncSpacesWiki(client, projectToken, title);

    }
}
