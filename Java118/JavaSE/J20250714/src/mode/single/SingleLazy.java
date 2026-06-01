package mode.single;

public class SingleLazy {
    private static SingleLazy singleLazy;

    private SingleLazy() {

    }

    // 线程不安全
    public static SingleLazy getInstance() {
        if (singleLazy == null) {
            singleLazy = new SingleLazy();
        }

        return singleLazy;
    }
}
