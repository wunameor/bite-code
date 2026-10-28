package com.wunamor.logistics.listener;



import com.wunamor.common.entity.OrderInfo;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "order.queue")
public class OrderListener {

    @RabbitHandler
    public void listener(String message) {
        System.out.println("接收信息：" + message);
    }

    @RabbitHandler
    public void listener(OrderInfo orderInfo) {
        System.out.println("接收信息：" + orderInfo);
    }
}
