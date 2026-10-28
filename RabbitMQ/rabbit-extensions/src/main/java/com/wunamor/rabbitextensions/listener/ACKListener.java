package com.wunamor.rabbitextensions.listener;


import com.rabbitmq.client.Channel;
import com.wunamor.rabbitextensions.constants.Constant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ACKListener {
    @RabbitListener(queues = Constant.ACK_QUEUE)
    public void listen(Message message, Channel channel) {
        System.out.printf("获取到 rabbit ack 消息, [%s]: %s, tag: %d\n", Constant.ACK_QUEUE,
                new String(message.getBody()), message.getMessageProperties().getDeliveryTag());


        System.out.println("消息业务处理");
    }
}
