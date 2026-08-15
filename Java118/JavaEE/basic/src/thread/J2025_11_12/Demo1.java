package thread.J2025_11_12;

public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        Thread mainThread = Thread.currentThread();

        Thread t = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println("hello thread");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            }
            System.out.println("mainThread.state = " + mainThread.getState());
        });

        System.out.println("t.state = " + t.getState());
        t.start();
        System.out.println("t.state = " + t.getState());
//        t.join();
        t.join(1000);
        System.out.println("t.state = " + t.getState());

    }
}
