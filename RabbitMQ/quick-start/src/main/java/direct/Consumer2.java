package direct;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;

public class Consumer2 {
    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQConfig.getChannel();

        channel.queueDeclare(Constant.DIRECT_QUEUE2, true, false, false, null);

        channel.basicConsume(Constant.DIRECT_QUEUE2, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        });

    }
}
