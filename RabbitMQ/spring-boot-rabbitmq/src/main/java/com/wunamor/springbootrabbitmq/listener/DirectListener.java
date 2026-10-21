package com.wunamor.springbootrabbitmq.listener;

import com.wunamor.springbootrabbitmq.constants.Constant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DirectListener {

    @RabbitListener(queues = Constant.DIRECT_QUEUE1)
    public void fanoutLister1(String message) {
        System.out.println("queue["+Constant.DIRECT_QUEUE1+"]: " + message);
    }

    @RabbitListener(queues = Constant.DIRECT_QUEUE2)
    public void fanoutLister2(String message) {
        System.out.println("queue["+Constant.DIRECT_QUEUE2+"]: " + message);

    }
}
