package J2025_11_26;

class MyBlockingQueue {
    private String[] elems;
    private int size;
    private int head;
    private int last;
    private Object locker = new Object();
    public MyBlockingQueue(int length) {
        this.elems = new String[length];
    }

    public synchronized String take() throws InterruptedException {
        synchronized (locker) {
            while (size == 0) {
                locker.wait();
            }

            String ret = elems[head];
            elems[head++] = null;
            if (head >= elems.length) {
                head = 0;
            }
            size--;
            locker.notify();
            return ret;
        }
    }

    public void put(String val) throws InterruptedException {
        synchronized (locker) {
            while (size == elems.length) {
                locker.wait();
            }

            elems[last++] = val;
            if (last == elems.length) last = 0;

            size++;
            locker.notify();
        }
    }
}

public class Demo2 {

    public static void main(String[] args) throws InterruptedException {
        MyBlockingQueue queue = new MyBlockingQueue(500);


        Thread producer = new Thread(() -> {
            int count = 0;
            while (true) {
                try {
                    queue.put(count + "");
                    System.out.println("producer put: count = " + count);
                    count++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("consumer take: count = " + queue.take());
//                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        producer.start();
        consumer.start();

    }
}
