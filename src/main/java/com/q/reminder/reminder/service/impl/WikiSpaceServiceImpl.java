package com.q.reminder.reminder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lark.oapi.service.wiki.v2.model.Node;
import com.q.reminder.reminder.entity.WikiSpace;
import com.q.reminder.reminder.mapper.WikiSpaceMapping;
import com.q.reminder.reminder.service.WikiSpaceService;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.SpaceWikoServiceImpl
 * @Description :
 * @date :  2022.11.18 14:36
 */
@Service
public class WikiSpaceServiceImpl extends ServiceImpl<WikiSpaceMapping, WikiSpace> implements WikiSpaceService {

    @Autowired
    private BaseFeishu baseFeishu;
    @Override
    public WikiSpace syncSpacesWiki(String projectToken, String title) throws Exception {
        Node node = baseFeishu.wiki().syncSpacesWiki(projectToken, title);
        WikiSpace space = new WikiSpace();
        BeanUtil.copyProperties(node, space);
        return space;
    }

    @Override
    public WikiSpace getSpacesNode(String token) throws Exception {
        Node node =  baseFeishu.wiki().getSpacesNode(token);
        WikiSpace space = new WikiSpace();
        BeanUtil.copyProperties(node, space);
        return space;
    }
}
