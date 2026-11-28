package com.wunamor.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// 单元测试不依赖真实 Nacos，关闭注册发现和配置中心，避免本地未启动 Nacos 时测试失败
@SpringBootTest(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.nacos.discovery.username=test",
        "spring.cloud.nacos.discovery.password=test",
        "spring.cloud.nacos.config.username=test",
        "spring.cloud.nacos.config.password=test"
})
class OrderServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
