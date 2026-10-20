package work;

import com.rabbitmq.client.Channel;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Produce {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 配置信息
        Channel channel = RabbitMQConfig.getChannel();


        // 配置交换机/队列 交换机使用的是默认的，因此不需要申明了
        channel.queueDeclare(Constant.WORK_QUEUE, true, false, false, null);

        for (int i = 0; i < 10; i++) {
            // 默认的交换机是把内容发送到与 routineKey 一样的队列
            String msg = "hi rabbitmq: " + Constant.WORK_QUEUE + " " + i;
            channel.basicPublish("", Constant.WORK_QUEUE, null, msg.getBytes());
        }

        // 释放资源
        RabbitMQConfig.close();
    }
}
