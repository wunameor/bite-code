package com.wunamor.bookdemo.interceptor;

import com.wunamor.bookdemo.constants.Constant;
import com.wunamor.bookdemo.enums.ResultCodeEnum;
import com.wunamor.bookdemo.model.Result;
import com.wunamor.bookdemo.model.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
//@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("login interceptor preHandle...");

        response.setContentType("application/json;charset=utf-8");

        HttpSession session = request.getSession();
        UserInfo userInfo = (UserInfo) session.getAttribute(Constant.USER_LOGIN_INFO);
        if (userInfo == null || userInfo.getId() == null) {
            log.warn("用户信息不存在，请登录");
            response.getOutputStream().write(objectMapper.writeValueAsBytes(Result.fail(ResultCodeEnum.NO_LOGIN)));
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        log.info("login interceptor postHandle...");
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        log.info("login interceptor afterCompletion...");
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
