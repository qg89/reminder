package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.mapper.GroupInfoMapping;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.vo.ChatProjectVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.MemberInfoServiceImpl
 * @Description :
 * @date :  2022.09.27 13:24
 */
@Service
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapping, GroupInfo> implements GroupInfoService {

    @Override
    public List<ChatProjectVo> listByProect(String pKey) {
        return baseMapper.listByProject(pKey);
    }
}
