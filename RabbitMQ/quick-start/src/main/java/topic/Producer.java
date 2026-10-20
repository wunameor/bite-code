package topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;

public class Producer {
    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQConfig.getChannel();

        channel.queueDeclare(Constant.TOPIC_QUEUE1, true, false, true, null);
        channel.queueDeclare(Constant.TOPIC_QUEUE2, true, false, true, null);
        channel.exchangeDeclare(Constant.TOPIC_EXCHANGE, BuiltinExchangeType.TOPIC, true);

        // 绑定
        channel.queueBind(Constant.TOPIC_QUEUE1, Constant.TOPIC_EXCHANGE, "*.a.*");
        channel.queueBind(Constant.TOPIC_QUEUE2, Constant.TOPIC_EXCHANGE, "#.m");
        channel.queueBind(Constant.TOPIC_QUEUE2, Constant.TOPIC_EXCHANGE, "m.*");
        channel.queueBind(Constant.TOPIC_QUEUE2, Constant.TOPIC_EXCHANGE, "*.m");

        // 发送
        String msgA = "hello rabbitmq, my routing key is m.a.awda";
        channel.basicPublish(Constant.TOPIC_EXCHANGE, "m.a.awda", null, msgA.getBytes());

        String msgB = "hello rabbitmq, my routing key is adaw.m"; // 只会接收到一个
        channel.basicPublish(Constant.TOPIC_EXCHANGE, "adaw.m", null, msgB.getBytes());

        System.out.println("发送成功✌️");

        RabbitMQConfig.close();
    }
}
