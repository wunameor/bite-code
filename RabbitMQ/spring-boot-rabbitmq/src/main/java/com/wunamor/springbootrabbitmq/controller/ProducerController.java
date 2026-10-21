package com.wunamor.springbootrabbitmq.controller;

import com.wunamor.springbootrabbitmq.constants.Constant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

}
