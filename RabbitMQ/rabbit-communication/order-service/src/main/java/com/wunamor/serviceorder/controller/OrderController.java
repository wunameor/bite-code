package com.wunamor.serviceorder.controller;

import com.wunamor.common.entity.OrderInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/create")
    public String create() {
        // 一些列业务先不做了，这里只有订单发送


        rabbitTemplate.convertAndSend("", "order.queue", "添加订单成功，订单Id: " + UUID.randomUUID().toString());
        return "添加订单成功";
    }

    @RequestMapping("/create2")
    public String create2() {
        // 一些列业务先不做了，这里只有订单发送
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setName("order-name-" + new Random().nextInt(100));
        orderInfo.setOrderId(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend("", "order.queue", orderInfo);
        return "添加订单成功";
    }
}
