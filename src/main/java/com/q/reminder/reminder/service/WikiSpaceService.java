package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.WikiSpace;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.SpaceWikoService
 * @Description :
 * @date :  2022.11.18 14:35
 */
public interface WikiSpaceService extends IService<WikiSpace> {
    WikiSpace syncSpacesWiki(String projectToken, String title) throws Exception;

    WikiSpace getSpacesNode(String token);
}
