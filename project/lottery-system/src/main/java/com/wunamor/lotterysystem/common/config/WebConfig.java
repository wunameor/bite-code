package com.wunamor.lotterysystem.common.config;

import com.wunamor.lotterysystem.common.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    private final List<String> excludePath = List.of(
            "/**/*.html",
            "/css/**",
            "/js/**",
            "/pic/**",
            "/favicon.ico",
            "/error",
            "/.well-known/**",
            "/user/login",
            "/user/email-code"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }
}
