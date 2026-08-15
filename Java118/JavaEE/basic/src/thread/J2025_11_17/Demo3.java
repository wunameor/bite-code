package thread.J2025_11_17;

import java.util.Vector;

public class Demo3 {
    public static void main(String[] args) {
        Vector<String> vector = new Vector<>();
        Thread t1 = new Thread(() -> {
            // 这样就是线程不安全的
            if (vector.isEmpty()) {
                vector.add("test1");
            }
        });

        Thread t2 = new Thread(() -> {
            // 这样就是线程不安全的
            if (vector.isEmpty()) {
                vector.add("test2");
            }
        });
    }
}
