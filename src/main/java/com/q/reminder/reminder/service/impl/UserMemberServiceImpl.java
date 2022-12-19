package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.mapper.UserMapping;
import com.q.reminder.reminder.service.UserMemberService;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.UserServiceImpl
 * @Description :
 * @date :  2022.09.23 14:32
 */
@Service
public class UserMemberServiceImpl extends ServiceImpl<UserMapping, UserMemgerInfo> implements UserMemberService {
}
