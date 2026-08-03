package J2025_11_10;

import java.util.Scanner;

public class Demo1 {
    private static boolean running = true;

    public static void main(String[] args) {
        // 变更在这里 ⬇️
//        boolean running = true; // 这样是否可以正常运行呢？
        Thread t = new Thread(() -> {
            while (running) {
                System.out.println("hello thread");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("thread end...");
        });

        t.start();

        Scanner in = new Scanner(System.in);

        in.next(); // 仅仅是为了阻塞主线程
        // 输入任意值后，自动把 running 的值变为 false 从而关闭 t 这个线程
        running = false;
    }
}
