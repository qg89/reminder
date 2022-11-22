package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lark.oapi.Client;
import com.q.reminder.reminder.entity.WikiSpace;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.SpaceWikoService
 * @Description :
 * @date :  2022.11.18 14:35
 */
public interface WikiSpaceService extends IService<WikiSpace> {
    WikiSpace syncSpacesWiki(Client client, String projectToken, String title) throws Exception;
}
