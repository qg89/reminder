package com.q.reminder.reminder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.User;
import com.q.reminder.reminder.mapper.UserInfoMapping;
import com.q.reminder.reminder.service.LoginService;
import com.q.reminder.reminder.vo.LoginParam;
import com.q.reminder.reminder.vo.UpdatePasswordVo;
import com.q.reminder.reminder.vo.base.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired
    private UserInfoMapping userMapper;

    @PostMapping("/login")
    public ResultUtil login(@RequestBody LoginParam loginParam){
        return loginService.login(loginParam);
    }

    @PostMapping("/update_p")
    public ResultUtil update(@RequestBody UpdatePasswordVo vo){
        LambdaQueryWrapper<User> lq = Wrappers.<User>lambdaQuery();
        lq.eq(User::getUsername, vo.getUsername());
        User selectOne = userMapper.selectOne(lq);
        if (selectOne == null) {
            return ResultUtil.fail(500, "密码修改失败，未查询到此用户");
        }
        String password = vo.getPassword();
        BCryptPasswordEncoder en = new BCryptPasswordEncoder();
        selectOne.setPassword(en.encode(password));
        userMapper.updateById(selectOne);
        return ResultUtil.success("修改成功");
    }
}
