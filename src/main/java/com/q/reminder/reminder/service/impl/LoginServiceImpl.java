package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.User;
import com.q.reminder.reminder.mapper.UserInfoMapping;
import com.q.reminder.reminder.service.LoginService;
import com.q.reminder.reminder.util.JWTUtil;
import com.q.reminder.reminder.vo.LoginParam;
import com.q.reminder.reminder.vo.UserInfoVo;
import com.q.reminder.reminder.vo.UserLogin;
import com.q.reminder.reminder.vo.base.ResultUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.LoginServiceImpl
 * @Description :
 * @date :  2022.11.17 14:18
 */
@Service
public class LoginServiceImpl extends ServiceImpl<UserInfoMapping, User> implements LoginService {
    @Autowired
    private JWTUtil jjwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserInfoMapping userInfoMapping;

    @Override
    public ResultUtil login(LoginParam loginParam) {
        //进行用户认证。获取AuthenticationManager authenticate
        //获取认证对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginParam.getUsername(), loginParam.getPassword());
        //认证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //认证失败
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        //认证成功，生成token
        //获取用户信息（getPrincipal()）
        UserLogin user = (UserLogin) authenticate.getPrincipal();
        Long id = user.getUser().getId();
        Claims claims = Jwts.claims();
        claims.put("userId", id);
        jjwtUtil.defaultBuilder(jjwtUtil);
        String token = jjwtUtil.createToken(claims);
        //返回
        UserInfoVo info = userInfoMapping.userInfo(user.getUsername());
        info.setToken(token);
        return ResultUtil.success("登录成功", info);
    }
}
