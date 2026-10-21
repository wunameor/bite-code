package rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;

public class ServeRPC {
    public static void main(String[] args) throws IOException {
        Channel channel = RabbitMQConfig.getChannel();

        channel.queueDeclare(Constant.RPC_RESPONSE_QUEUE, true, false, false, null);
        channel.queueDeclare(Constant.RPC_REQUEST_QUEUE, true, false, false, null);

        channel.basicQos(1);
        channel.basicConsume(Constant.RPC_REQUEST_QUEUE, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = "服务端接送到消息：" + new String(body) + " properties.getReplyTo(): " + properties.getReplyTo();
                System.out.println(msg);
                // 发送给客户端
                AMQP.BasicProperties prop = new AMQP.BasicProperties().builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
                channel.basicPublish("", properties.getReplyTo(), prop, msg.getBytes());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });

    }
}
