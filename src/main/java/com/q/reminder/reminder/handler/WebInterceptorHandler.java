package com.q.reminder.reminder.handler;

import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.mapper.UserInfoMapping;
import com.q.reminder.reminder.service.LoginService;
import com.q.reminder.reminder.util.IpUtils;
import com.q.reminder.reminder.util.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handler.Interceptor
 * @Description :
 * @date :  2023/7/20 13:36
 */
@Component
@Log4j2
public class WebInterceptorHandler implements HandlerInterceptor {

    @Autowired
    private UserInfoMapping userInfoMapping;
    @Autowired
    private LoginService loginService;
    @Autowired
    private JWTUtil jjwtUtil;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String strToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(strToken)) {
            Claims claims = jjwtUtil.getTokenPayLoad(strToken);
            String userToken = claims.toString();
            Integer userID = Integer.getInteger(claims.getId());
            request.setAttribute("LOGIN_USER_ID", userID);
            request.setAttribute("LOGIN_USER_TOKEN", userToken);
        }


        String verifySignParam = getVerifySignParam(request);
        MethodParameter[] methodParameters = ((HandlerMethod) handler).getMethodParameters();
        String username;
        if (StringUtils.isNotBlank(verifySignParam)) {
            username = JSONObject.parseObject(verifySignParam).getString("username");
        } else {
            username = getCurrentLoginUser();
        }
        boolean flag = true;
        if (StringUtils.isBlank(username)) {
           return false;
        }
        String ip = loginService.getByUserNameToIp(username);
        String remoteAddr = IpUtils.getIp(request);
        if (!remoteAddr.contains(ip)) {
            flag = false;
        }
        return flag;
    }

    public String getVerifySignParam(HttpServletRequest request) throws IOException {
        BodyHttpServletRequestWrapper bodyHttpServletRequestWrapper = (BodyHttpServletRequestWrapper) request;
        return bodyHttpServletRequestWrapper.getBody();
    }

    /**
     * 获取当前登录用户
     * @return
     */
    private String getCurrentLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("当前无用户登录");
        } else {
            return authentication.getName();
        }
    }
}
