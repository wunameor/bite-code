package J2025_11_10;

public class Demo3 {
    private static int result;

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            int sum = 0;
            for (int i = 1; i <= 1000; i++) {
                sum += i;
            }
            result = sum;
        });

        t.start();
        t.join(1000);

        System.out.println("result = " + result);
    }
}
