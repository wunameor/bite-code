import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProduceDemo {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 配置信息
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("study");
        factory.setPassword("study");
        factory.setVirtualHost("bite");

        // 获取连接
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 配置交换机/队列 交换机使用的是默认的，因此不需要申明了
        channel.queueDeclare("queue1", true, false, false, null);

        for (int i = 0; i < 10; i++) {
            // 默认的交换机是把内容发送到与 routineKey 一样的队列
            String msg = "hi rabbitmq: " + i;
            channel.basicPublish("", "queue1", null, msg.getBytes());
        }

        // 释放资源
        channel.close();
        connection.close();
    }
}
