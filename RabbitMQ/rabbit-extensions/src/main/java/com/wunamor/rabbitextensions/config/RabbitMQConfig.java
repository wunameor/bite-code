package com.wunamor.rabbitextensions.config;

import com.wunamor.rabbitextensions.constants.Constant;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
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
}
