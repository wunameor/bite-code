package J2025_11_24;

public class FastReorderTest {
    // 共享变量
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    // 核心：使用自增的轮次标识，彻底避免 new Thread() 的开销
    private static volatile int round = 0;
    // 用于记录两个线程当前执行完毕的轮次
    private static volatile int t1Round = 0;
    private static volatile int t2Round = 0;

    public static void main(String[] args) throws InterruptedException {
        // 常驻工作线程 1
        Thread t1 = new Thread(() -> {
            int currentRound = 0;
            while (true) {
                // 自旋等待主线程开启新的一轮
                while (round == currentRound) {}
                currentRound = round; // 获取最新轮次

                // --- 核心测试代码 ---
                x = 1;
                a = y;

                // 标记本轮执行完毕
                t1Round = currentRound;
            }
        });

        // 常驻工作线程 2
        Thread t2 = new Thread(() -> {
            int currentRound = 0;
            while (true) {
                // 自旋等待主线程开启新的一轮
                while (round == currentRound) {}
                currentRound = round;

                // --- 核心测试代码 ---
                y = 1;
                b = x;

                // 标记本轮执行完毕
                t2Round = currentRound;
            }
        });

        // 设为守护线程，主线程退出时自动结束
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();

        // 给系统调度器一点时间，确保两个线程都已经跑起来并进入等待状态
        Thread.sleep(100);

        int currentRound = 0;
        long startTime = System.currentTimeMillis();

        while (true) {
            currentRound++;

            // 1. 重置共享变量
            x = 0; y = 0; a = 0; b = 0;

            // 2. 开启新的一轮！(由于 t1 和 t2 都在极高频自旋，它们会瞬间同时捕获到这个变化)
            round = currentRound;

            // 3. 自旋等待 t1 和 t2 都执行完这一轮
            while (t1Round != currentRound || t2Round != currentRound) {}

            // 4. 检查结果
            if (a == 0 && b == 0) {
                long cost = System.currentTimeMillis() - startTime;
                System.out.println("======================================");
                System.out.println("★ 瞬间捕获到指令重排序 (Store-Load)！");
                System.out.println("执行轮次: 第 " + currentRound + " 次");
                System.out.println("总计耗时: " + cost + " 毫秒");
                System.out.println("======================================");
                break;
            }

            // 因为执行极快，每 100 万次才打印一次，避免 IO 拖慢速度
            if (currentRound % 1000000 == 0) {
                System.out.println("已超高速执行 " + currentRound + " 次...");
            }
        }
    }
}
