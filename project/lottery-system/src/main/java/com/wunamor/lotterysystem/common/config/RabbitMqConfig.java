package com.wunamor.lotterysystem.common.config;

import com.wunamor.lotterysystem.common.constants.RabbitMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 抽奖队列配置：抽奖命令和通知各有一条主队列，处理失败后进入各自的死信队列。
 */
@Configuration
public class RabbitMqConfig {

    /** 持久化 DirectExchange，抽奖命令和通知通过不同 routing key 路由。 */
    @Bean
    public DirectExchange drawExchange() {
        return new DirectExchange(RabbitMqConstants.DRAW_EXCHANGE, true, false);
    }

    /** 中奖通知主队列；消费失败后转发到通知死信队列。 */
    @Bean
    public Queue drawNotifyQueue() {
        return QueueBuilder.durable(RabbitMqConstants.DRAW_NOTIFY_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DRAW_DLX)
                .withArgument("x-dead-letter-routing-key", RabbitMqConstants.DRAW_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding drawNotifyBinding() {
        return BindingBuilder.bind(drawNotifyQueue())
                .to(drawExchange())
                .with(RabbitMqConstants.DRAW_ROUTING_KEY);
    }

    /** 抽奖命令主队列；消费失败后转发到命令死信队列。 */
    @Bean
    public Queue drawCommandQueue() {
        return QueueBuilder.durable(RabbitMqConstants.DRAW_COMMAND_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMqConstants.DRAW_DLX)
                .withArgument("x-dead-letter-routing-key",
                        RabbitMqConstants.DRAW_COMMAND_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding drawCommandBinding() {
        return BindingBuilder.bind(drawCommandQueue())
                .to(drawExchange())
                .with(RabbitMqConstants.DRAW_COMMAND_ROUTING_KEY);
    }

    /** 持久化死信交换机，命令和通知的失败消息共用。 */
    @Bean
    public DirectExchange drawDeadLetterExchange() {
        return new DirectExchange(RabbitMqConstants.DRAW_DLX, true, false);
    }

    /** 中奖通知死信队列。 */
    @Bean
    public Queue drawDeadLetterQueue() {
        return QueueBuilder.durable(RabbitMqConstants.DRAW_DLQ).build();
    }

    @Bean
    public Binding drawDeadLetterBinding() {
        return BindingBuilder.bind(drawDeadLetterQueue())
                .to(drawDeadLetterExchange())
                .with(RabbitMqConstants.DRAW_DLQ_ROUTING_KEY);
    }

    /** 抽奖命令死信队列。 */
    @Bean
    public Queue drawCommandDeadLetterQueue() {
        return QueueBuilder.durable(RabbitMqConstants.DRAW_COMMAND_DLQ).build();
    }

    @Bean
    public Binding drawCommandDeadLetterBinding() {
        return BindingBuilder.bind(drawCommandDeadLetterQueue())
                .to(drawDeadLetterExchange())
                .with(RabbitMqConstants.DRAW_COMMAND_DLQ_ROUTING_KEY);
    }
}
