package demo1;

public class Father {
    public Father() {
        func();
    }

    public void func() {
        System.out.println("Father func()...");
    }

    public static void main(String[] args) {
        new Son(); // 打印什么？
    }
}

class Son extends Father {
    private int age = 1;

    @Override
    public void func() {
        System.out.println("Son func... age = " + age);
    }
}
