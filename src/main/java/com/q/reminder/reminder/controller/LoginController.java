package com.q.reminder.reminder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.User;
import com.q.reminder.reminder.mapper.UserInfoMapping;
import com.q.reminder.reminder.service.LoginService;
import com.q.reminder.reminder.vo.LoginParam;
import com.q.reminder.reminder.vo.UpdatePasswordVo;
import com.q.reminder.reminder.vo.base.ResultUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final UserInfoMapping userMapper;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResultUtil login(@RequestBody LoginParam loginParam) {
        return loginService.login(loginParam);
    }

    @PostMapping("/update_p")
    public ResultUtil update(@RequestBody UpdatePasswordVo vo) {
        BCryptPasswordEncoder en = new BCryptPasswordEncoder();
        String username = vo.getUsername();
        String password = vo.getPassword();
        String newPd = vo.getNewPd();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String pd = userDetails.getPassword();
        if (!en.matches(password, pd)) {
            throw new AccountExpiredException("密码错误");
        }
        if (newPd.length() >= 20) {
            return ResultUtil.fail("密码长度不能大于20！");
        }
        LambdaQueryWrapper<User> lq = Wrappers.<User>lambdaQuery();
        lq.eq(User::getUsername, username);
        User user = userMapper.selectOne(lq);
        user.setPassword(newPd);
        userMapper.updateById(user);
        return ResultUtil.success("修改成功");
    }
}
