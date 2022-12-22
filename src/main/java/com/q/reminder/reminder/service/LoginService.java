package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.User;
import com.q.reminder.reminder.vo.LoginParam;
import com.q.reminder.reminder.vo.base.ResultUtil;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.LoginService
 * @Description :
 * @date :  2022.11.17 14:19
 */
public interface LoginService extends IService<User> {
    ResultUtil login(LoginParam loginParam);
}
