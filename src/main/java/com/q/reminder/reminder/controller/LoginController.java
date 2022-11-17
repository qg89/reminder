package com.q.reminder.reminder.controller;

import com.q.reminder.reminder.service.LoginService;
import com.q.reminder.reminder.vo.LoginParam;
import com.q.reminder.reminder.vo.base.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.LoginController
 * @Description :
 * @date :  2022.11.17 14:45
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResultUtil login(@RequestBody LoginParam loginParam){
        return loginService.login(loginParam);
    }
}
