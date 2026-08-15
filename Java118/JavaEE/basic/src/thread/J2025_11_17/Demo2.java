package thread.J2025_11_17;

public class Demo2 {
    public static void main(String[] args) {
        Object lockA = new Object();
        Object lockB = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (lockB) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lockA) {
                    System.out.println("t1");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lockA) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (lockB) {
                    System.out.println("t2");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
