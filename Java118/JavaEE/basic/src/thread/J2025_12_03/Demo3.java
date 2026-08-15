package thread.J2025_12_03;

import java.util.PriorityQueue;
import java.util.TimerTask;

class MyTimer {
    class MyTimerTask implements Comparable<MyTimerTask>{
        private Runnable task;
        private long time;

        public MyTimerTask(Runnable task, long delay) {
            this.task = task;
            this.time = delay + System.currentTimeMillis();
        }

        public long getTime() {
            return time;
        }

        public void run() {
            task.run();
        }

        @Override
        public int compareTo(MyTimerTask o) {
            return (int) (this.time - o.time);
        }
    }

    // 时间用时间戳来存储，这样就不需要更新
    private PriorityQueue<MyTimerTask> queue = new PriorityQueue<>();

    public MyTimer() {
        // 创建一个线程 需要一直执行
        Thread t = new Thread(() -> {
            while (true) {
                // 由于涉及到 对 queue 的添加与删除，那么需要用锁来保证线程安全
                synchronized (this) {
                    while (queue.isEmpty()) {
                        // 建议使用 while 通过判断两次来判断 wait 是 interrupt 打断的还是通过 notify 来唤醒的
                        // 不要使用 continue ，因为这样会导致忙等，是 CPU 一直运行，而且会干扰到 schedule
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    MyTimerTask task = queue.peek();
                    long currentTime = System.currentTimeMillis();
                    if (currentTime >= task.getTime()) {
                        // 执行
                        task.run();
                        queue.poll();
                    } else {
                        // 等待，但是是有一定的时间的
                        // 不建议使用阻塞队列，因为这里必须要 wait 那么就需要管理两把锁，容易死锁
                        try {
                            this.wait(task.getTime() - currentTime);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        t.start();
    }

    public void schedule(Runnable task, long delay) {
        // 加锁
        synchronized (this) {
            queue.add(new MyTimerTask(task, delay));
            this.notify();
        }
    }
}

public class Demo3 {
    public static void main(String[] args) {
        MyTimer timer = new MyTimer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello timer 4");
            }
        }, 4000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello timer 3");
            }
        }, 3000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello timer 2");
            }
        }, 2000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("hello timer 1");
            }
        }, 1000);

        System.out.println("hello main");
    }
}
