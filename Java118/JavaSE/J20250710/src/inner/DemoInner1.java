package inner;

// 静态内部类
public class DemoInner1 {
    private static int value1 = 1;
    public int value2 = 2;


    static class Inner {
        public static int value3 = 3;
        public static void test() {
            System.out.println(value1);
            System.out.println(new DemoInner1().value2);
            System.out.println(value3);

        }
    }
}
