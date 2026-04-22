public class Test {
    public static void main(String[] args) {
        int a = 1;
//        a = ++a;
        a = a++;
        System.out.println(a);
        int temp = a;
        a = a + 1;
        a = temp;
    }
}
