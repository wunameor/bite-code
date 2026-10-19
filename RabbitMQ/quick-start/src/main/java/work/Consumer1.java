package work;

import com.rabbitmq.client.*;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1 {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQConfig.getChannel();

        // 声明队列（可以不声明，但是为了提高代码的健壮性，建议声明）
        channel.queueDeclare(Constant.WORK_QUEUE, true, false, false, null);



        channel.basicConsume(Constant.WORK_QUEUE, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费：" + new String(body));
            }
        });

//        RabbitMQConfig.close();
    }
}
