package basic;

import com.rabbitmq.client.*;
import config.RabbitMQConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerDemo {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQConfig.getChannel();
        // 声明队列（可以不声明，但是为了提高代码的健壮性，建议声明）
        channel.queueDeclare("queue1", true, false, false, null);



        channel.basicConsume("queue1", true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println(new String(body));
            }
        });

        Thread.sleep(2000);
        RabbitMQConfig.close();
    }
}
