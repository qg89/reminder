package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.vo.SendUserByGroupVo;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.UserService
 * @Description :
 * @date :  2022.09.23 14:30
 */
public interface UserMemberService extends IService<UserMemgerInfo> {
    Boolean saveOrUpdateBatchAll(List<UserMemgerInfo> membersByChats);

    /**
     * 查询可用发生群内的成员信息
     * @return
     */
    List<SendUserByGroupVo> queryUserGroupList();

}
