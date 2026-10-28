package com.wunamor.logistics.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * 消费端也声明队列：否则 logistics 比 order-service 先启动时，
     * 队列不存在会导致监听容器启动失败。
     * 声明参数必须与生产端完全一致（同名 + durable）。
     */
    @Bean("orderQueue")
    public Queue orderQueue() {
        return QueueBuilder.durable("order.queue").build();
    }

    /**
     * 只要容器里存在唯一的 MessageConverter Bean，
     * Spring Boot 就会自动把它设置到 RabbitTemplate 和 @RabbitListener 容器工厂上，
     * 所以不需要再手动定义 RabbitTemplate。
     *
     * spring-amqp 4.1+ 默认只信任 [java.util, java.lang] 这两个包里的 __TypeId__，
     * 消费自定义 DTO 时必须显式把 DTO 所在包加入白名单，否则会报
     * "The class '...OrderInfo' is not in the trusted packages"。
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
        typeMapper.setTrustedPackages("com.wunamor.common.entity", "java.util", "java.lang");

        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
