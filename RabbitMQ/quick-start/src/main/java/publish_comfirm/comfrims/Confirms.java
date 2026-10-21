package publish_comfirm.comfrims;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import config.RabbitMQConfig;
import constants.Constant;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class Confirms {

    private static final int PUBLISH_COUNT = 20000;

    public static void main(String[] args) throws IOException, InterruptedException, TimeoutException {
//        publishingMessagesIndividually();

//        publishingMessagesInBatches();

        handlingPublisherConfirmsAsynchronously();
    }

    /**
     * 批量确认
     */
    private static void publishingMessagesInBatches() throws IOException, InterruptedException, TimeoutException {
        Channel channel = RabbitMQConfig.getChannel();
        channel.confirmSelect();
        channel.queueDeclare(Constant.PUBLISH_CONFIRM_QUEUE2, true, false, false, null);

        // 临界值
        int batchCount = 100;
        int currentPublishCount = 0;

        long start = System.currentTimeMillis();
        for (int i = 0; i < PUBLISH_COUNT; i++) {
            String msg = "hello publish confirm " + 1;
            channel.basicPublish("", Constant.PUBLISH_CONFIRM_QUEUE2, null, msg.getBytes());
            currentPublishCount++;
            if (batchCount == currentPublishCount) {
                channel.waitForConfirms(5_000);
                currentPublishCount = 0;
            }
        }
        if (currentPublishCount > 0) {
            channel.waitForConfirms(5_000);
        }
        long end = System.currentTimeMillis();
        System.out.println("publishingMessagesInBatches 执行时间: " + (end - start));

    }

    /**
     * 单独确认
     */
    private static void publishingMessagesIndividually() throws IOException, InterruptedException, TimeoutException {
        Channel channel = RabbitMQConfig.getChannel();
        channel.confirmSelect();
        channel.queueDeclare(Constant.PUBLISH_CONFIRM_QUEUE1, true, false, false, null);

        long start = System.currentTimeMillis();
        for (int i = 0; i < PUBLISH_COUNT; i++) {
            String msg = "hello publish confirm " + 1;
            channel.basicPublish("", Constant.PUBLISH_CONFIRM_QUEUE1, null, msg.getBytes());
            channel.waitForConfirms(5_000);
        }
        long end = System.currentTimeMillis();
        System.out.println("publishingMessagesIndividually 执行时间: " + (end - start));
    }

    /**
     * 异步确认
     */
    private static void handlingPublisherConfirmsAsynchronously() throws IOException {
        Channel channel = RabbitMQConfig.getChannel();
        channel.queueDeclare(Constant.PUBLISH_CONFIRM_QUEUE3, true, false, false, null);

        long start = System.currentTimeMillis();
        channel.confirmSelect();
        final SortedSet<Long> noConfirm = Collections.synchronizedSortedSet(new TreeSet<>());

        ConfirmListener confirmListener = new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    noConfirm.headSet(deliveryTag + 1).clear();
                } else {
                    noConfirm.remove(deliveryTag);
                }
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    noConfirm.headSet(deliveryTag + 1).clear();
                } else {
                    noConfirm.remove(deliveryTag);
                }
            }
        };
        channel.addConfirmListener(confirmListener);


        for (int i = 0; i < PUBLISH_COUNT; i++) {
            String msg = "hello publish confirm " + 1;
            noConfirm.add(channel.getNextPublishSeqNo());
            channel.basicPublish("", Constant.PUBLISH_CONFIRM_QUEUE3, null, msg.getBytes());
        }
        long end = System.currentTimeMillis();

        System.out.println("handlingPublisherConfirmsAsynchronously 执行时间: " + (end - start));
    }
}
