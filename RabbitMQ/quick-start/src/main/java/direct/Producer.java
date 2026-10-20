package direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;

public class Producer {
    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQConfig.getChannel();

        channel.queueDeclare(Constant.DIRECT_QUEUE1, true, false, false, null);
        channel.queueDeclare(Constant.DIRECT_QUEUE2, true, false, false, null);
        channel.exchangeDeclare(Constant.DIRECT_EXCHANGE, BuiltinExchangeType.DIRECT, true);

        // 绑定
        channel.queueBind(Constant.DIRECT_QUEUE1, Constant.DIRECT_EXCHANGE, "a");
        channel.queueBind(Constant.DIRECT_QUEUE2, Constant.DIRECT_EXCHANGE, "a");
        channel.queueBind(Constant.DIRECT_QUEUE2, Constant.DIRECT_EXCHANGE, "b");
        channel.queueBind(Constant.DIRECT_QUEUE2, Constant.DIRECT_EXCHANGE, "c");

        // 发送
        String msgA = "hello rabbitmq, my routing key is a";
        channel.basicPublish(Constant.DIRECT_EXCHANGE, "a", null, msgA.getBytes());

        String msgB = "hello rabbitmq, my routing key is b";
        channel.basicPublish(Constant.DIRECT_EXCHANGE, "b", null, msgB.getBytes());

        String msgC = "hello rabbitmq, my routing key is c";
        channel.basicPublish(Constant.DIRECT_EXCHANGE, "c", null, msgC.getBytes());

        System.out.println("发送成功✌️");

        RabbitMQConfig.close();
    }
}
