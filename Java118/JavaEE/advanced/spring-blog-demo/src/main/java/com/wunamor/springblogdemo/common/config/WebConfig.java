package com.wunamor.springblogdemo.common.config;

import com.wunamor.springblogdemo.common.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    private List<String> excludePattern = List.of(
            "/**/login",
            "/blog-editormd/**",
            "/css/**",
            "/js/**",
            "/pic/**",
            "/**/*.html",
            "favicon.ico"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePattern);
    }
}
