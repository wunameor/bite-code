package com.wunamor.lotterysystem.common.config;

import com.wunamor.lotterysystem.common.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private UploadProperties uploadProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = List.of(
                "/**/*.html",
                "/css/**",
                "/js/**",
                "/pic/**",
                uploadProperties.getUrlPrefix() + "/**",
                "/favicon.ico",
                "/error",
                "/.well-known/**",
                "/user/login",
                "/user/email-code",
                "/user/reset-password",
                "/winning-records/show"
        );

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }

    /**
     * 把本地上传目录映射成 /uploads/**，图片可以直接通过 URL 访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String urlPrefix = uploadProperties.getUrlPrefix();
        if (urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix.substring(0, urlPrefix.length() - 1);
        }

        String location = Paths.get(uploadProperties.getDir())
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();
        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler(urlPrefix + "/**")
                .addResourceLocations(location);
    }
}
