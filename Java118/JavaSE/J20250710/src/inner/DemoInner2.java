package inner;

// 实例内部类
public class DemoInner2 {
    private static int value1 = 1;
    public int value2 = 2;

    class Inner {
        public static int value1 = 2;
        public static int value3 = 3;
        public void test() {
            System.out.println(value1);
            System.out.println(DemoInner2.value1);
            System.out.println(DemoInner2.this.value2);
            System.out.println(new DemoInner2().value2);
            System.out.println(value3);

        }
    }

    void test() {
        System.out.println(DemoInner2.this.value2);
        System.out.println(this.value2);
    }
}
