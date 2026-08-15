package thread.J2025_12_01;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo1 {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10000; i++) {
            int id = i;
            service.submit(() -> {
                System.out.println("第 " + id + " 次执行的线程为：" + Thread.currentThread().getName());
            });
        }

    }
}
