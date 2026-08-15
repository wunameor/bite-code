package thread.J2025_12_03;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class MyThreadPool {
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

    public MyThreadPool(int size) {
        for (int i = 0; i < size; i++) {
            // 创建线程
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Runnable take = workQueue.take();
                        take.run();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            t.start();
        }
    }

    public void submit(Runnable runnable) throws InterruptedException {
        workQueue.put(runnable);
    }
}


public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        MyThreadPool pool = new MyThreadPool(10);
        for (int i = 0; i < 10000; i++) {
            int id = i;
            pool.submit(() -> {
                System.out.println("第 " + id + " 次执行的线程为：" + Thread.currentThread().getName());
            });
        }
    }
}
