package thread.J2025_11_05;

public class Demo3 {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            while (true) {
                System.out.println("hello thread");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 设置为 后台线程
        t.setDaemon(true);

        t.start();
//        Thread.sleep(3000);
    }
}
