package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.User;
import com.q.reminder.reminder.mapper.UserInfoMapping;
import com.q.reminder.reminder.vo.UserLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.UserDetailsServiceImpl
 * @Description :
 * @date :  2022.11.17 14:06
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl extends ServiceImpl<UserInfoMapping, User> implements UserDetailsService {

    private final UserInfoMapping userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username).last("limit 1");
        User user = userMapper.selectOne(queryWrapper);
        //异常
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("用户名未发现");
        }
        //查询对应权限信息
        String password = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(password);
        //数据封装为UserDetails返回
        return new UserLogin(user);
    }
}
