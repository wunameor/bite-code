package thread.J2025_11_24;

public class ReorderTestOptimized {
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    // 关键点 1：使用 volatile 变量作为无锁的“发令枪”
    private static volatile boolean start = false;

    public static void main(String[] args) throws InterruptedException {
        int count = 0;

        while (true) {
            count++;
            x = 0; y = 0; a = 0; b = 0;
            start = false; // 每次循环重置发令枪

            Thread t1 = new Thread(() -> {
                // 关键点 2：忙等待（自旋）。不让线程休眠，疯狂消耗 CPU 等待信号
                while (!start) {}

                // 信号变为 true，瞬间冲出
                x = 1;
                a = y;
            });

            Thread t2 = new Thread(() -> {
                while (!start) {}

                y = 1;
                b = x;
            });

            t1.start();
            t2.start();

            // 给操作系统的线程调度器一点点时间，确保 t1 和 t2 都已经运行到了 while(!start) 处
            Thread.yield();

            // 开枪！由于 t1 和 t2 都在自旋，它们会极快地同时捕获到这个变化
            start = true;

            t1.join();
            t2.join();

            if (a == 0 && b == 0) {
                System.out.println("在第 " + count + " 次执行时，成功捕获到了指令重排序 (Store Buffer 延迟)！");
                break;
            }

            if (count % 10000 == 0) {
                System.out.println("已执行 " + count + " 次...");
            }
        }
    }
}
