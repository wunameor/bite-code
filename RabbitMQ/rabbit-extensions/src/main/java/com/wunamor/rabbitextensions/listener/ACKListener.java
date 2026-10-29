package com.wunamor.rabbitextensions.listener;


import com.rabbitmq.client.Channel;
import com.wunamor.rabbitextensions.constants.Constant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ACKListener {
    @RabbitListener(queues = Constant.ACK_QUEUE)
    public void listen(Message message, Channel channel) throws IOException {
        System.out.printf("获取到 rabbit ack 消息, [%s]: %s, tag: %d\n", Constant.ACK_QUEUE,
                new String(message.getBody()), message.getMessageProperties().getDeliveryTag());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.println("消息业务处理");
            int k = 3 / 0;
            channel.basicAck(deliveryTag, false);
            System.out.println("消息业务完成");
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
//            throw e;
        }

    }
}
