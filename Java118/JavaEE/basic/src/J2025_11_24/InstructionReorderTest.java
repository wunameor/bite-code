package J2025_11_24;

import java.util.concurrent.CountDownLatch;

public class InstructionReorderTest {
    // 共享变量
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        int count = 0;
        int maxCount = 1000000;
        while (count <= 1000000) {
            count++;

            // 每次循环前重置状态
            x = 0; y = 0;
            a = 0; b = 0;

            // 使用 CountDownLatch 作为一个“发令枪”，让两个线程尽可能在同一纳秒级同时起跑
            CountDownLatch startGate = new CountDownLatch(1);

            Thread t1 = new Thread(() -> {
                try {
                    startGate.await(); // 等待发令枪响
                } catch (InterruptedException e) {}

                // --- 核心逻辑 ---
                x = 1; // 步骤 1：写 x
                a = y; // 步骤 2：读 y
            });

            Thread t2 = new Thread(() -> {
                try {
                    startGate.await(); // 等待发令枪响
                } catch (InterruptedException e) {}

                // --- 核心逻辑 ---
                y = 1; // 步骤 3：写 y
                b = x; // 步骤 4：读 x
            });

            t1.start();
            t2.start();

            // 扣动发令枪，两个线程同时执行
            startGate.countDown();

            // 等待两个线程执行完毕
            t1.join();
            t2.join();

            // 检查结果
            if (a == 0 && b == 0) {
                System.out.println("在第 " + count + " 次执行时，捕获到了指令重排序！ -> a=0, b=0");
                break; // 捕获到就退出循环
            }

            if (count % 10000 == 0) {
                System.out.println("已执行 " + count + " 次...");
            }
        }

        System.out.println("执行了 " + maxCount + " 次依然不出现，默认无法指令重排序");
    }
}
