package J2025_11_05;

public class Demo1 {
    public static void main(String[] args) {
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

        // 通过 debug 来查看
        t.setName("this is a name");

        t.start();

    }
}
