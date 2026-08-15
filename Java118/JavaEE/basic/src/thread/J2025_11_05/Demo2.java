package thread.J2025_11_05;

public class Demo2 {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("hello thread");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        System.out.println("执行之前： isAlive = " + t.isAlive());
        t.start();
        Thread.sleep(1000);
        System.out.println("执行之后： isAlive = " + t.isAlive());
        Thread.sleep(4000);
        System.out.println("执行结束： isAlive = " + t.isAlive());
    }
}
