package com.wunamor.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 订单服务基础 Bean 配置。
 */
@Configuration
public class BeanConfig {

    /**
     * 创建支持服务名调用的 RestTemplate。
     * 加上 @LoadBalanced 后，请求 http://product-service/... 会交给 LoadBalancer 解析实例。
     *
     * @return 带负载均衡能力的 RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
