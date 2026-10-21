package com.wunamor.springbootrabbitmq.controller;

import com.wunamor.springbootrabbitmq.constants.Constant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/producer")
@RestController
public class ProducerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/work")
    public String work() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(Constant.WORK_QUEUE, "spring boot rabbitmq work queue " + i);
        }

        return "发送成功";
    }

    @RequestMapping("/fanout")
    public String fanout() {
        rabbitTemplate.convertAndSend(Constant.FANOUT_EXCHANGE, "", "hello rabbit mq fanout...");

        return "发送成功";
    }

    @RequestMapping("/direct/{routingKey}")
    public String direct(@PathVariable("routingKey") String routingKey) {
        rabbitTemplate.convertAndSend(Constant.DIRECT_EXCHANGE, routingKey, "hello rabbit mq direct. my routing key is " + routingKey);

        return "发送成功";
    }

    @RequestMapping("/topic/{topicKey}")
    public String topic(@PathVariable("topicKey") String topicKey) {
        rabbitTemplate.convertAndSend(Constant.TOPIC_EXCHANGE, topicKey, "hello rabbit mq topic. my routing key is " + topicKey);
        return "发送成功";
    }

}
