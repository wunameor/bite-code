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
            if (size == 0) {
                locker.wait();
            }

            String ret = elems[head];
            elems[head++] = null;
            if (head >= elems.length) {
                head = 0;
            }
            size++;
            locker.notify();
            return ret;
        }
    }

    public void put(String val) throws InterruptedException {
        synchronized (locker) {
            if (size == elems.length) {
                locker.wait();
            }

            elems[last++] = val;
            if (last == elems.length) last = 0;

            size--;
            locker.notify();
        }
    }
}

public class Demo2 {

    public static void main(String[] args) throws InterruptedException {
        MyBlockingQueue queue = new MyBlockingQueue(3);

        queue.put("hello");
        queue.put("hello2");
        queue.put("hello3");
        queue.put("hello4");
    }
}
