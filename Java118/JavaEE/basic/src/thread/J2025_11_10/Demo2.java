package thread.J2025_11_10;

import java.util.Scanner;

public class Demo2 {


    public static void main(String[] args) {
        Thread t = new Thread(() -> {

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("hi thread");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        t.start();
        Scanner in = new Scanner(System.in);
        in.next();
        t.interrupt();

    }
}
