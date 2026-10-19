package fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;

public class Producer {
    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQConfig.getChannel();

        channel.exchangeDeclare(Constant.FANOUT_EXCHANGE, BuiltinExchangeType.FANOUT, true);
        channel.queueDeclare(Constant.FANOUT_QUEUE1, true, false, false, null);
        channel.queueDeclare(Constant.FANOUT_QUEUE2, true, false, false, null);
        channel.queueBind(Constant.FANOUT_QUEUE1, Constant.FANOUT_EXCHANGE, "");
        channel.queueBind(Constant.FANOUT_QUEUE2, Constant.FANOUT_EXCHANGE, "");

        String msg = "hello fanout mode...";
        channel.basicPublish(Constant.FANOUT_EXCHANGE, "", null, msg.getBytes());

        RabbitMQConfig.close();
    }
}
