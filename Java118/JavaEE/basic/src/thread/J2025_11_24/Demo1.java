package thread.J2025_11_24;

class SingletonLazy {
    private static volatile SingletonLazy instance;

    public static SingletonLazy getInstance() {
        // 问题：为什么这里是有两个 if 判定？这两个 if 判定的作用分别是什么？
        if (instance == null) {
            synchronized (SingletonLazy.class) {
                if (instance == null) {
                    instance = new SingletonLazy();
                }
            }
        }
        return instance;
    }

    private SingletonLazy() {
    }
}

public class Demo1 {
    public static void main(String[] args) {
        SingletonLazy instance = SingletonLazy.getInstance();
        // instance.func();
    }
}
