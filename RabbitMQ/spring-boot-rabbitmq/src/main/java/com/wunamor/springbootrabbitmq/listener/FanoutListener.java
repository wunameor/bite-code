package com.wunamor.springbootrabbitmq.listener;

import com.wunamor.springbootrabbitmq.constants.Constant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FanoutListener {

    @RabbitListener(queues = Constant.FANOUT_QUEUE1)
    public void fanoutLister1(String message) {
        System.out.println("queue1: " + message);
    }

    @RabbitListener(queues = Constant.FANOUT_QUEUE2)
    public void fanoutLister2(String message) {
        System.out.println("queue2: " + message);
    }
}
