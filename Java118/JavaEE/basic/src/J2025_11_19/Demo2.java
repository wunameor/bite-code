package J2025_11_19;

import java.util.Scanner;

public class Demo2 {
    public static void main(String[] args) {
        Object locker = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (locker) {
                System.out.println("t1 wait 之前");
                try {
                    locker.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("t1 wait 之后");
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (locker) {
                System.out.println("t2 wait 之前");
                try {
                    locker.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("t2 wait 之后");
            }
        });

        Thread t3 = new Thread(() -> {
            synchronized (locker) {
                System.out.println("t3 wait 之前");
                try {
                    locker.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("t3 wait 之后");
            }
        });

        Thread t4 = new Thread(() -> {
            Scanner in = new Scanner(System.in);
            in.next();
            synchronized (locker) {
//                locker.notify(); // 随机解锁一个
                locker.notifyAll(); // 随机解锁一个
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}
