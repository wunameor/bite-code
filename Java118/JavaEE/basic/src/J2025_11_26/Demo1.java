package J2025_11_26;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class Demo1 {
    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(500);

        Thread productor = new Thread(() -> {
            int count = 0;
            while (true) {
                System.out.println("生产：val = " + count);
                try {
                    queue.put(count++ + "");
//                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("消费：val = " + queue.take());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        consumer.start();
        productor.start();
    }
}
