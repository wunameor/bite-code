package com.wunamor.rabbitextensions.controller;

import com.wunamor.rabbitextensions.constants.Constant;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Resource(name = "confirmRabbitTemplate")
    private RabbitTemplate confirmRabbitTemplate;

    @Resource(name = "returnRabbitTemplate")
    private RabbitTemplate returnRabbitTemplate;

    @RequestMapping("/ack")
    public String ack() {
        rabbitTemplate.convertAndSend(Constant.ACK_EXCHANGE, "ack", "this is an 'ack' test");

        return "发送成功";
    }

    @RequestMapping("/durable")
    public String durable() {
        Message message = new Message("This is a durable test".getBytes(), new MessageProperties());
        // 数据非持久化
//        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
        // 数据持久化
        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        rabbitTemplate.convertAndSend(Constant.DURABLE_EXCHANGE, "durable", message);
        return "发送成功";
    }

    @RequestMapping("/confirm")
    public String confirm() {
        CorrelationData correlationData = new CorrelationData("1");
        confirmRabbitTemplate.convertAndSend(Constant.CONFIRM_EXCHANGE, "confirm1", "this is a publisher confirm test", correlationData);
        return "发送成功";
    }

    @RequestMapping("/return")
    public String ret() {
        CorrelationData correlationData = new CorrelationData("6");
        returnRabbitTemplate.convertAndSend(Constant.CONFIRM_EXCHANGE, "confirm1", "this is a publisher confirm test", correlationData);
        return "发送成功";
    }
}
