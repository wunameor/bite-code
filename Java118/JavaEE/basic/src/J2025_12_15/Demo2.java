package J2025_12_15;

import java.util.concurrent.CountDownLatch;

public class Demo2 {
    public static void main(String[] args) throws InterruptedException {
        int total = 7;

        CountDownLatch downLatch = new CountDownLatch(total);
        for (int i = 1; i <= total; i++) {
            int id = i;
            new Thread(() -> {
                System.out.println("执行 id = " + id + " 的线程");
                downLatch.countDown();
            }).start();
        }

        downLatch.await();
        System.out.println("全部线程执行结束");

    }
}
