package com.wunamor.rabbitextensions.config;

import com.wunamor.rabbitextensions.constants.Constant;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // 消息确认
    @Bean("ackQueue")
    public Queue ackQueue() {
        return QueueBuilder.durable(Constant.ACK_QUEUE).build();
    }

    @Bean("ackExchange")
    public DirectExchange ackExchange() {
        return ExchangeBuilder.directExchange(Constant.ACK_EXCHANGE).build();
    }

    @Bean("ackBindingackBinding")
    public Binding ackBinding(@Qualifier("ackExchange") DirectExchange ackExchange, @Qualifier("ackQueue") Queue ackQueue) {
        return BindingBuilder.bind(ackQueue).to(ackExchange).with("ack");
    }

    // 持久化
    @Bean("durableQueue")
    public Queue durableQueue() {
        return QueueBuilder.nonDurable(Constant.DURABLE_QUEUE).build();
    }

    @Bean("durableExchange")
    public DirectExchange durableExchange() {
        return ExchangeBuilder.directExchange(Constant.DURABLE_EXCHANGE).durable(false).build();
    }

    @Bean("durableBinding")
    public Binding durableBinding(@Qualifier("durableExchange") DirectExchange durableExchange, @Qualifier("durableQueue") Queue durableQueue) {
        return BindingBuilder.bind(durableQueue).to(durableExchange).with("durable");
    }

    // 发送方确认
    @Bean("confirmQueue")
    public Queue confirmQueue() {
        return QueueBuilder.durable(Constant.CONFIRM_QUEUE).build();
    }

    @Bean("confirmExchange")
    public DirectExchange confirmExchange() {
        return ExchangeBuilder.directExchange(Constant.CONFIRM_EXCHANGE).build();
    }

    @Bean("confirmBinding")
    public Binding confirmBinding(@Qualifier("confirmExchange") DirectExchange confirmExchange, @Qualifier("confirmQueue") Queue confirmQueue) {
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with("confirm");
    }
}
