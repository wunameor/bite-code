package com.wunamor.orderservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于验证 Nacos 配置中心读取和动态刷新的接口。
 */
@RestController
@RequestMapping("/order")
@RefreshScope
public class OrderConfigController {

    /**
     * 从 Nacos 的 order-service.yaml 读取；本地没有配置时使用默认值
     */
    @Value("${demo.message:local-default}")
    private String message;

    /**
     * 返回当前生效的示例配置。
     *
     * @return 配置值
     */
    @GetMapping("/config")
    public String config() {
        return message;
    }
}
