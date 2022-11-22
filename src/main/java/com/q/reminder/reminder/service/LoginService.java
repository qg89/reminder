package com.q.reminder.reminder.service;

import com.q.reminder.reminder.vo.LoginParam;
import com.q.reminder.reminder.vo.base.ResultUtil;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.LoginService
 * @Description :
 * @date :  2022.11.17 14:19
 */
public interface LoginService {
    ResultUtil login(LoginParam loginParam);
}
