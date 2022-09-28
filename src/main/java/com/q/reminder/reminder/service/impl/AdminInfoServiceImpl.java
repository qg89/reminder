package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.AdminInfo;
import com.q.reminder.reminder.mapper.AdminInfoMapping;
import com.q.reminder.reminder.service.AdminInfoService;
import org.springframework.stereotype.Service;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.AdminInfoServiceImpl
 * @Description :
 * @date :  2022.09.27 14:43
 */
@Service
public class AdminInfoServiceImpl extends ServiceImpl<AdminInfoMapping, AdminInfo> implements AdminInfoService {
}
