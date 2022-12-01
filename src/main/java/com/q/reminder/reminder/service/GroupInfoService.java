package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.vo.ChatProjectVo;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.MemberInfoService
 * @Description :
 * @date :  2022.09.27 13:23
 */
public interface GroupInfoService extends IService<GroupInfo> {
    List<ChatProjectVo> listByProect(String pKey);
}
