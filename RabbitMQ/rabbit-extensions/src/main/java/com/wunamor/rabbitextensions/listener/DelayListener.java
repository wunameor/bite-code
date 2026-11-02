package com.wunamor.rabbitextensions.listener;


import com.rabbitmq.client.Channel;
import com.wunamor.rabbitextensions.constants.Constant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class DelayListener {
    // 模拟延迟队列，监听死信队列
    @RabbitListener(queues = Constant.DELAY_QUEUE)
    public void listen(Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.printf("[%tc] 获取到 rabbit dl 消息, [%s]: %s, tag: %d\n",
                new Date(), Constant.DELAY_QUEUE, new String(message.getBody()), deliveryTag);
    }
}
