package com.wunamor.rabbitextensions.listener;


import com.rabbitmq.client.Channel;
import com.wunamor.rabbitextensions.constants.Constant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class QosListener {
    @RabbitListener(queues = Constant.QOS_QUEUE)
    public void listen1(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.printf("1111[%tc] 获取到 rabbit qos 消息, [%s]: %s, tag: %d\n",
                    new Date(), Constant.QOS_QUEUE, new String(message.getBody()), deliveryTag);
            channel.basicAck(deliveryTag, false);
            Thread.sleep(1000);

        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = Constant.QOS_QUEUE)
    public void listen2(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.printf("2222[%tc] 获取到 rabbit qos 消息, [%s]: %s, tag: %d\n",
                    new Date(), Constant.QOS_QUEUE, new String(message.getBody()), deliveryTag);
            Thread.sleep(2000);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
