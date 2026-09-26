package com.wunamor.springblogdemo.common.interceptor;

import com.wunamor.springblogdemo.common.constants.Constant;
import com.wunamor.springblogdemo.common.enums.ResultCodeEnums;
import com.wunamor.springblogdemo.common.exception.BlogException;
import com.wunamor.springblogdemo.common.pojo.response.Result;
import com.wunamor.springblogdemo.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Configuration
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        response.setContentType("application/json;charset=UTF-8");
        // 校验JWT是否有效
        try {
            String userToken = request.getHeader(Constant.HEADER_USER_TOKEN);
            Claims claims = JwtUtil.parseJwt(userToken);
            return true;
        } catch (BlogException e) {
            log.error("令牌解析失败，e: ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(objectMapper.writeValueAsString(Result.fail(ResultCodeEnums.USER_NO_LOGIN)));
            return false;
        }
    }
}
