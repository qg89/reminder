package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.UserP;
import com.q.reminder.reminder.mapper.UserPMapping;
import com.q.reminder.reminder.service.UserPService;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.UserPServiceImpl
 * @Description :
 * @date :  2022.12.21 19:28
 */
@Service
public class UserPServiceImpl extends ServiceImpl<UserPMapping, UserP> implements UserPService {
}
