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

    // 重试机制
    @Bean("retryQueue")
    public Queue retryQueue() {
        return QueueBuilder.durable(Constant.RETRY_QUEUE).build();
    }

    @Bean("retryExchange")
    public DirectExchange retryExchange() {
        return ExchangeBuilder.directExchange(Constant.RETRY_EXCHANGE).build();
    }

    @Bean("retryBinding")
    public Binding retryBinding(@Qualifier("retryExchange") DirectExchange retryExchange, @Qualifier("retryQueue") Queue retryQueue) {
        return BindingBuilder.bind(retryQueue).to(retryExchange).with("retry");
    }

    // 过期时间 TTL
    @Bean("ttlQueue")
    public Queue ttlQueue() {
        return QueueBuilder.durable(Constant.TTL_QUEUE).ttl(20_000).build();
    }

    @Bean("noTTLQueue")
    public Queue noTTLQueue() {
        return QueueBuilder.durable(Constant.NO_TTL_QUEUE).build();
    }


    @Bean("ttlExchange")
    public DirectExchange ttlExchange() {
        return ExchangeBuilder.directExchange(Constant.TTL_EXCHANGE).build();
    }

    @Bean("ttlBinding")
    public Binding ttlBinding(@Qualifier("ttlExchange") DirectExchange ttlExchange, @Qualifier("ttlQueue") Queue ttlQueue) {
        return BindingBuilder.bind(ttlQueue).to(ttlExchange).with("ttl");
    }

    @Bean("noTTLBinding")
    public Binding noTTLBinding(@Qualifier("ttlExchange") DirectExchange ttlExchange, @Qualifier("noTTLQueue") Queue ttlQueue) {
        return BindingBuilder.bind(ttlQueue).to(ttlExchange).with("ttl");
    }

    // 死信
    @Bean("normalQueue")
    public Queue normalQueue() {
        return QueueBuilder
                .durable(Constant.NORMAL_QUEUE)
                .deadLetterExchange(Constant.DL_EXCHANGE)
                .ttl(10_000)
                .maxLength(10L)
                .deadLetterRoutingKey("dl")
                .build();
    }

    @Bean("normalExchange")
    public DirectExchange normalExchange() {
        return ExchangeBuilder.directExchange(Constant.NORMAL_EXCHANGE).build();
    }

    @Bean("normalBinding")
    public Binding normalBinding(@Qualifier("normalExchange") DirectExchange normalExchange, @Qualifier("normalQueue") Queue normalQueue) {
        return BindingBuilder.bind(normalQueue).to(normalExchange).with("normal");
    }

    @Bean("dlQueue")
    public Queue dlQueue() {
        return QueueBuilder.durable(Constant.DL_QUEUE).build();
    }

    @Bean("dlExchange")
    public DirectExchange dlExchange() {
        return ExchangeBuilder.directExchange(Constant.DL_EXCHANGE).build();
    }

    @Bean("dlBinding")
    public Binding dlBinding(@Qualifier("dlExchange") DirectExchange dlExchange, @Qualifier("dlQueue") Queue dlQueue) {
        return BindingBuilder.bind(dlQueue).to(dlExchange).with("dl");
    }

    // 延迟队列
    @Bean("delayQueue")
    public Queue delayQueue() {
        return QueueBuilder.durable(Constant.DELAY_QUEUE).build();
    }

    @Bean("delayExchange")
    public DirectExchange delayExchange() {
        return ExchangeBuilder.directExchange(Constant.DELAY_EXCHANGE)
                .delayed()
                .build();
    }

    @Bean("delayBinding")
    public Binding delayBinding(@Qualifier("delayExchange") DirectExchange delayExchange, @Qualifier("delayQueue") Queue delayQueue) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with("delay");
    }
}
