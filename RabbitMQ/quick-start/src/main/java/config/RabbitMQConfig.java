package config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import constants.Constant;

public class RabbitMQConfig {
    private static volatile ConnectionFactory factory;
    private static volatile Connection connection;
    private static volatile Channel channel;


    public static ConnectionFactory getFactory() {
        if (factory == null) {
            synchronized (RabbitMQConfig.class) {
                if (factory == null) {
                    factory = new ConnectionFactory();
                    factory.setUsername(Constant.USERNAME);
                    factory.setPassword(Constant.PASSWORD);
                    factory.setHost(Constant.HOST);
                    factory.setPort(Constant.PORT);
                    factory.setVirtualHost(Constant.VIRTUAL_HOST);
                }
            }
        }
        return factory;
    }

    public static Connection getConnection() {
        try {
            if (connection == null) {
                synchronized (RabbitMQConfig.class) {
                    if (connection == null) {
                        connection = getFactory().newConnection();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Channel getChannel() {
        try {
            if (connection == null) {
                synchronized (RabbitMQConfig.class) {
                    if (connection == null) {
                        channel = getConnection().createChannel();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    public static void close() {
        try {
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
