package thread.J2025_12_15;

import java.util.concurrent.Semaphore;

public class Demo1 {
    public static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                try {
                    semaphore.acquire();
                    count++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50000; i++) {
                try {
                    semaphore.acquire();
                    count++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(count);
    }

    public static void main1(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore( 4);

        semaphore.acquire();
        System.out.println("acquire1");
        semaphore.release();
        System.out.println("release1");
        semaphore.acquire();
        System.out.println("acquire2");
        semaphore.acquire();
        System.out.println("acquire3");
        semaphore.acquire();
        System.out.println("acquire4");
        semaphore.acquire();
        System.out.println("acquire5");
        // 这里就会阻塞
        semaphore.acquire();
        System.out.println("acquire6");
    }
}
