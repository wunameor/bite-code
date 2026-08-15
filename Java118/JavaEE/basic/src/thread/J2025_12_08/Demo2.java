package thread.J2025_12_08;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Demo2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int count = 0;
                for (int i = 0; i < 50000; i++) {
                    count++;
                }
                return count;
            }
        };

        FutureTask<Integer> futureTask = new FutureTask<>(callable);

        Thread t = new Thread(futureTask);
        t.start();

        System.out.println(futureTask.get());
    }
}
