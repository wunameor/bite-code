package com.wunamor.captchademo.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "captcha")
@Data
public class CaptchaConfiguration {
    private int width;
    private int height;

    private long TTL; // 验证码有效时间
}
