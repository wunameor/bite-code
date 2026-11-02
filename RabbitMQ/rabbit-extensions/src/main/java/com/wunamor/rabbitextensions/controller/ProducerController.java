package com.wunamor.rabbitextensions.controller;

import com.wunamor.rabbitextensions.constants.Constant;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Resource(name = "confirmRabbitTemplate")
    private RabbitTemplate confirmRabbitTemplate;

    @Resource(name = "returnRabbitTemplate")
    private RabbitTemplate returnRabbitTemplate;

    @Resource(name = "transTemplate")
    private RabbitTemplate transTemplate;

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

    @RequestMapping("/retry")
    public String retry() {
        returnRabbitTemplate.convertAndSend(Constant.RETRY_EXCHANGE, "retry", "this is a retry test");
        return "发送成功";
    }

    @RequestMapping("/ttl")
    public String ttl() {
        returnRabbitTemplate.convertAndSend(Constant.TTL_EXCHANGE, "ttl", "this is a ttl test", message -> {
            message.getMessageProperties().setExpiration("5000"); // 30s
            return message;
        });

        returnRabbitTemplate.convertAndSend(Constant.TTL_EXCHANGE, "ttl", "this is a ttl test", message -> {
            message.getMessageProperties().setExpiration("30000"); // 30s
            return message;
        });

        returnRabbitTemplate.convertAndSend(Constant.TTL_EXCHANGE, "ttl", "this is a ttl test", message -> {
            message.getMessageProperties().setExpiration("5000"); // 30s
            return message;
        });
        return "发送成功";
    }

    @RequestMapping("/noTTL")
    public String noTTL() {
        returnRabbitTemplate.convertAndSend(Constant.TTL_EXCHANGE, "ttl", "this is a ttl test", message -> {
            message.getMessageProperties().setExpiration("10000"); // 10s
            return message;
        });
        return "发送成功";
    }

    @RequestMapping("/dl")
    public String dl() {
//        for (int i = 0; i < 20; i++) {
//            returnRabbitTemplate.convertAndSend(Constant.NORMAL_EXCHANGE, "normal", "this is a dl test");
//        }
        System.out.printf("[%tc] 把消息发送到 rabbit dl 消息,\n", new Date());

        returnRabbitTemplate.convertAndSend(Constant.NORMAL_EXCHANGE, "normal", "this is a dl test");
        return "发送成功";
    }

    @RequestMapping("/delay")
    public String delay() {
        System.out.printf("[%tc] 把消息发送到 rabbit delay 消息,\n", new Date());
        returnRabbitTemplate.convertAndSend(Constant.DELAY_EXCHANGE, "delay", "this is a delay 10s test", message -> {
            message.getMessageProperties().setDelayLong(10_000L);
            return message;
        });

        returnRabbitTemplate.convertAndSend(Constant.DELAY_EXCHANGE, "delay", "this is a delay 30s test", message -> {
            message.getMessageProperties().setDelayLong(30_000L);
            return message;
        });
        return "发送成功";
    }

    @Transactional
    @RequestMapping("/trans")
    public String trans() {
        transTemplate.convertAndSend("", Constant.TRANS_QUEUE, "this is a trans test111");
        int a = 3 / 0;
        transTemplate.convertAndSend("", Constant.TRANS_QUEUE, "this is a trans test222");

        return "发送成功";
    }
}
