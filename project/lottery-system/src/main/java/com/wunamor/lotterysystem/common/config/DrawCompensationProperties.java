package com.wunamor.lotterysystem.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 抽奖任务补偿配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "lottery.draw.compensation")
public class DrawCompensationProperties {

    /**
     * 是否启用补偿扫描；本地调试可关闭，生产环境建议保持开启
     */
    private boolean enabled = true;

    /**
     * 单个任务最多补偿次数
     */
    private int maxAttempts = 3;

    /**
     * PENDING 任务超过该时长仍未处理时重新投递
     */
    private Duration pendingDelay = Duration.ofSeconds(10);

    /**
     * PROCESSING 任务超过该时长仍未完成时重置并重新投递
     */
    private Duration processingTimeout = Duration.ofMinutes(5);

    /**
     * 补偿扫描间隔
     */
    private Duration scanInterval = Duration.ofSeconds(30);
}
