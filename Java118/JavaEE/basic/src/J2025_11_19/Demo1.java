package J2025_11_19;

import java.util.Scanner;

public class Demo1 {
    private static volatile boolean flag = true;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (flag) {
                // 什么也不做，确保 load 是占时间消耗的大头
            }

            System.out.println("t1 end");
        });

        Thread t2 = new Thread(() -> {
            Scanner in = new Scanner(System.in);
            in.next();
            flag = false;
            System.out.println("t2 end, flag = " + flag);
        });

        t1.start();
        t2.start();
    }
}
