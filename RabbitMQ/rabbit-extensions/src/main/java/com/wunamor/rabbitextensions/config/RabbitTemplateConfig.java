package com.wunamor.rabbitextensions.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTemplateConfig {
    @Bean("rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    @Bean("confirmRabbitTemplate")
    public RabbitTemplate confirmRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 由于使用了 lambda 表达式，没有捕获外部变量，因此它是一个“静态”的，因此反复设置是不会出错的
        rabbitTemplate.setConfirmCallback(((correlationData, ack, cause) -> {
            if (ack) {
                // 肯定接收
                System.out.printf("接收到消息， id: %s \n", correlationData != null ? correlationData.getId() : "");
            } else {
                // 不肯定
                System.out.printf("接收到消息， id: %s, cause: %s\n",correlationData != null ? correlationData.getId() : "", cause);
            }
        }));
        return rabbitTemplate;
    }

    @Bean("returnRabbitTemplate")
    public RabbitTemplate returnRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true); // 必须要添加这个开关
        rabbitTemplate.setReturnsCallback((message -> {
            System.out.printf("消息被退回: %s\n", message);
        }));
        return rabbitTemplate;
    }
}
