package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.constant.RedisKeyContents;
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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
@RequiredArgsConstructor
public class LoginServiceImpl extends ServiceImpl<UserInfoMapping, User> implements LoginService {
    private final JWTUtil jjwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserInfoMapping userInfoMapping;

    @Override
    public ResultUtil login(LoginParam loginParam, String remoteAddr) {
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
        User userInfo = user.getUser();
        UserInfoVo vo = new UserInfoVo();
        Long id = userInfo.getId();
        Claims claims = Jwts.claims();
        claims.put("user", userInfo);
        jjwtUtil.defaultBuilder(jjwtUtil);
        String token = jjwtUtil.createToken(claims);
        //返回
        String ips = userInfo.getRemoteAddr();
        if (!ips.contains(remoteAddr)) {
            return ResultUtil.fail("IP鉴权失败");
        }
        vo.setUsername(userInfo.getName());
        vo.setToken(token);
        return ResultUtil.success("登录成功", vo);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyContents.USER_NAME_IP, key = "#username", unless = "#username == null or #result == null")
    public String getByUserNameToIp(String username) {
        User user = baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (user == null) {
            return null;
        }
        return user.getRemoteAddr();
    }

    @Override
    @Cacheable(cacheNames = RedisKeyContents.USER_NAME_IP, key = "#userID", unless = "#userID == null or #result == null")
    public String getUsernameById(Integer userID) {
        User user = baseMapper.selectById(userID);
        if (user == null) {
            return null;
        }
        return user.getUsername();
    }
}
