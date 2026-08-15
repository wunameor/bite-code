package thread.J2025_11_03;

public class Demo1 {
    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                System.out.println("hello thread");
                // 只能用 try-catch 因为 Runnable 中的 run 方法没有 throw 异常
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        while (true) {
            System.out.println("hello main");
            // 只能用 try-catch 因为 Runnable 中的 run 方法没有 throw 异常
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
