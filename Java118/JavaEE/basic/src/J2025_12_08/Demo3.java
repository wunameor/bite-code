package J2025_12_08;

import java.util.concurrent.locks.ReentrantLock;

public class Demo3 {
    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                try {
                    lock.lock();
                    count++;
                } finally {
                    lock.unlock();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                try {
                    lock.lock();
                    count++;
                } finally {
                    lock.unlock();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(count);
    }
}
