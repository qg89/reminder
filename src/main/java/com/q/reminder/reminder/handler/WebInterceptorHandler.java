package com.q.reminder.reminder.handler;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.User;
import com.q.reminder.reminder.mapper.UserInfoMapping;
import com.q.reminder.reminder.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String verifySignParam = getVerifySignParam(request);
        MethodParameter[] methodParameters = ((HandlerMethod) handler).getMethodParameters();
        if (!JSONUtil.isTypeJSON(verifySignParam) || StringUtils.isBlank(verifySignParam)) {
            return false;
        }
        String username = JSONObject.parse(verifySignParam).getString("username");
        String remoteAddr = IpUtils.getIp(request);
        log.info("remoteAddr:{}", remoteAddr);
        User user = userInfoMapping.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (user == null || !remoteAddr.contains(user.getRemoteAddr())) {
            return false;
        }
        return true;
    }

    public String getVerifySignParam(HttpServletRequest request) throws IOException {
        BodyHttpServletRequestWrapper bodyHttpServletRequestWrapper = (BodyHttpServletRequestWrapper) request;
        return bodyHttpServletRequestWrapper.getBody();
    }
}
