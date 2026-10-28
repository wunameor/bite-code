package com.wunamor.serviceorder.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean("orderQueue")
    public Queue orderQueue() {
        return QueueBuilder.durable("order.queue").build();
    }

    /**
     * 只声明 MessageConverter，Spring Boot 会自动把它设置到
     * RabbitTemplate 和 @RabbitListener 的容器工厂上。
     * 不要自己 new RabbitTemplate()，否则 Boot 的 RabbitAutoConfiguration 会整体退让。
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
