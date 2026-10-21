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

    // 路由模式
    @Bean("directQueue1")
    public Queue directQueue1() {
        return QueueBuilder.durable(Constant.DIRECT_QUEUE1).build();
    }

    @Bean("directQueue2")
    public Queue directQueue2() {
        return QueueBuilder.durable(Constant.DIRECT_QUEUE2).build();
    }

    @Bean("directExchange")
    public DirectExchange directExchange() {
        return ExchangeBuilder.directExchange(Constant.DIRECT_EXCHANGE).durable(true).build();
    }

    @Bean("directBindingQueue1Apple")
    public Binding directBindingQueue1Apple(@Qualifier("directExchange") DirectExchange directExchange, @Qualifier("directQueue1") Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).with("apple");
    }

    @Bean("directBindingQueue2Apple")
    public Binding directBindingQueue2Apple(@Qualifier("directExchange") DirectExchange directExchange, @Qualifier("directQueue2") Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).with("apple");
    }

    @Bean("directBindingQueue2Banana")
    public Binding directBindingQueue2Banana(@Qualifier("directExchange") DirectExchange directExchange, @Qualifier("directQueue2") Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).with("banana");
    }

    // 通配符模式
    @Bean("topicQueue1")
    public Queue topicQueue1() {
        return QueueBuilder.durable(Constant.TOPIC_QUEUE1).build();
    }

    @Bean("topicQueue2")
    public Queue topicQueue2() {
        return QueueBuilder.durable(Constant.TOPIC_QUEUE2).build();
    }

    @Bean("topicExchange")
    public TopicExchange topicExchange() {
        return ExchangeBuilder.topicExchange(Constant.TOPIC_EXCHANGE).durable(true).build();
    }

    @Bean("topicBindQueue1_1")
    public Binding topicBindQueue1_1(@Qualifier("topicExchange") TopicExchange topicExchange, @Qualifier("topicQueue1") Queue queue) {
        return BindingBuilder.bind(queue).to(topicExchange).with("*.orange.*");
    }
    @Bean("topicBindQueue1_2")
    public Binding topicBindQueue1_2(@Qualifier("topicExchange") TopicExchange topicExchange, @Qualifier("topicQueue1") Queue queue) {
        return BindingBuilder.bind(queue).to(topicExchange).with("black.#");
    }

    @Bean("topicBindQueue2_1")
    public Binding topicBindQueue2_1(@Qualifier("topicExchange") TopicExchange topicExchange, @Qualifier("topicQueue2") Queue queue) {
        return BindingBuilder.bind(queue).to(topicExchange).with("*.*.rabbit");
    }
}
