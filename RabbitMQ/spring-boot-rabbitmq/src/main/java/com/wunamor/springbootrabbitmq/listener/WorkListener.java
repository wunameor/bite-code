package com.wunamor.springbootrabbitmq.listener;

import com.wunamor.springbootrabbitmq.constants.Constant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WorkListener {
    @RabbitListener(queues = Constant.WORK_QUEUE)
    public void workListener1(Message message) {
        System.out.println(message);
    }

    @RabbitListener(queues = Constant.WORK_QUEUE)
    public void workListener2(String message) {
        System.out.println(message);
    }
}
