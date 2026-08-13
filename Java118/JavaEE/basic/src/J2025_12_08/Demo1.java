package J2025_12_08;

import java.util.concurrent.atomic.AtomicInteger;

public class Demo1 {
    private static AtomicInteger safeCount = new AtomicInteger(0);
    private static int unsafeCount = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                safeCount.getAndIncrement(); // 等价与 count++
                unsafeCount++;
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                safeCount.getAndIncrement(); // 等价与 count++
                unsafeCount++;
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("safeCount = " + safeCount.get());
        System.out.println("unsafeCount = " + unsafeCount);
    }
}
