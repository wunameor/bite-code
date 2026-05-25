package inner;

public class Test {
    public static void main(String[] args) {
        new DemoInner4().test();
    }













    public static void main3(String[] args) {
        new DemoInner3() {
            @Override
            public void test() {
                System.out.println("匿名内部类");
            }
        }.test();
    }







    public static void main2(String[] args) {
        DemoInner2.Inner inner = new DemoInner2().new Inner();
        inner.test();
    }







    public static void main1(String[] args) {
        DemoInner1.Inner.test();
    }
}






