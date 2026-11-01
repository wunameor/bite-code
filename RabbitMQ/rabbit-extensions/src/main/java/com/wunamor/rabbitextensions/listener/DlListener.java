package com.wunamor.rabbitextensions.listener;


import com.rabbitmq.client.Channel;
import com.wunamor.rabbitextensions.constants.Constant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
public class DlListener {
    @RabbitListener(queues = Constant.NORMAL_QUEUE)
    public void listen(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.printf("获取到 rabbit dl 消息, [%s]: %s, tag: %d\n", Constant.NORMAL_QUEUE, new String(message.getBody()), deliveryTag);
        try {
            // 手动异常
            int a = 3 / 0;
            // 重试次数只能在配置中配置 acknowledge-mode: auto 的时候才能起作用
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
