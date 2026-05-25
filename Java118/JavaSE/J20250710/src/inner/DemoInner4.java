package inner;

// 局部内部类（不常用）
public class DemoInner4 {
    private int value1 = 1;
    public void test() {
        class Inner {
            private int value1 = 2;
            private int value2 = 3;
            void test() {
                System.out.println(DemoInner4.this.value1);
                System.out.println(value1);
                System.out.println(value2);
            }
        }
        new Inner().test();
    }
}
