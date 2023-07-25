package com.q.reminder.reminder.handler;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.SUserLog;
import com.q.reminder.reminder.entity.User;
import com.q.reminder.reminder.mapper.UserInfoMapping;
import com.q.reminder.reminder.service.LoginService;
import com.q.reminder.reminder.service.SUserLogService;
import com.q.reminder.reminder.util.IpUtils;
import com.q.reminder.reminder.util.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.LinkedHashMap;

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
    @Autowired
    private SUserLogService logService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String username;
        User user;
        String strToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        /**
         从请求头中取出token，
         @see com.q.reminder.reminder.config.WebConfiguration
         */
        if (StringUtils.isNotBlank(strToken)) {
            Claims claims = jjwtUtil.getTokenPayLoad(strToken);
            String userToken = claims.toString();
            LinkedHashMap<String, Object> map = claims.get("user", LinkedHashMap.class);
            user = BeanUtil.fillBeanWithMap(map, new User(), false);
            username = user.getUsername();
        } else {
            log.info("[handler]获取token为空！");
            return false;
        }
        boolean flag = true;
        if (StringUtils.isBlank(username)) {
            response.setStatus(401);
            log.info("[handler] 用户名称为空");
           return false;
        }
        String ips = user.getRemoteAddr();
        String ip = IpUtils.getIp(request);
        if (StringUtils.isBlank(ips) || !ips.contains(ip)) {
            log.info("[handler]IP鉴权失败,{}", ip);
            flag = false;
        }
        try {
            SUserLog userLog = new SUserLog();
            userLog.setUserId(user.getId());
            userLog.setParams(getVerifySignParam(request));
            userLog.setRequestUrl(request.getRequestURL().toString());
            logService.save(userLog);
        } catch (Exception e) {
            log.error("[拦截器]保存用户日志失败", e);
        }
        return flag;
    }

    public String getVerifySignParam(HttpServletRequest request) throws IOException {
        BodyHttpServletRequestWrapper bodyHttpServletRequestWrapper = (BodyHttpServletRequestWrapper) request;
        String body = bodyHttpServletRequestWrapper.getBody();
        if (StringUtils.isBlank(body)) {
            body = JSONObject.from(bodyHttpServletRequestWrapper.getParameterMap()).toJSONString();
        }
        return body;
    }
}
