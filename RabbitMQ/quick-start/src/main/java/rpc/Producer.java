package rpc;

import com.rabbitmq.client.*;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Producer {
    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQConfig.getChannel();

        channel.queueDeclare(Constant.RPC_REQUEST_QUEUE, true, false, true, null);
        channel.queueDeclare(Constant.RPC_RESPONSE_QUEUE, true, false, true, null);

        String correlationId = UUID.randomUUID().toString();

        AMQP.BasicProperties prop = new AMQP.BasicProperties()
                .builder()
                .correlationId(correlationId)
                .replyTo(Constant.RPC_RESPONSE_QUEUE)
                .build();

        String msg = "hello rpc...";
        channel.basicPublish("", Constant.RPC_REQUEST_QUEUE, prop, msg.getBytes());

        // 阻塞接收
        final BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        channel.basicConsume(Constant.RPC_RESPONSE_QUEUE, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String responseMsg = new String(body);
                System.out.println("接收到 response queue 信息：" + responseMsg);
                blockingQueue.offer(responseMsg);
            }
        });

        System.out.println("接收到阻塞队列的信息：" + blockingQueue.poll()); // 一般不会设置死等，但这里是学习
    }
}
