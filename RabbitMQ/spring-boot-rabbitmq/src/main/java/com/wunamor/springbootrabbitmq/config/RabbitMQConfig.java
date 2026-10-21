package com.wunamor.springbootrabbitmq.config;

import com.wunamor.springbootrabbitmq.constants.Constant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean("workQueue")
    public Queue workQueue() {
        return QueueBuilder.durable(Constant.WORK_QUEUE).build();
    }

    // 发布订阅
    @Bean("fanoutExchange")
    public FanoutExchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange(Constant.FANOUT_EXCHANGE).durable(true).build();
    }

    @Bean("fanoutQueue1")
    public Queue fanoutQueue1() {
        return QueueBuilder.durable(Constant.FANOUT_QUEUE1).build();
    }

    @Bean("fanoutQueue2")
    public Queue fanoutQueue2() {
        return QueueBuilder.durable(Constant.FANOUT_QUEUE2).build();
    }

    @Bean("fanoutBindingQueue1")
    public Binding fanoutBindingQueue1(@Qualifier("fanoutExchange") FanoutExchange fanoutExchange, @Qualifier("fanoutQueue1") Queue queue) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean("fanoutBindingQueue2")
    public Binding fanoutBindingQueue2(@Qualifier("fanoutExchange") FanoutExchange fanoutExchange, @Qualifier("fanoutQueue2") Queue queue) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
}
