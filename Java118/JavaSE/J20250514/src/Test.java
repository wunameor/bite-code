public class Test {
    public static int sum(int n) {
        if (n < 10) {
            return n;
        }
        return n % 10 + sum(n / 10);
    }

    public static void main(String[] args) {
        System.out.println(sum(1729));
    }

    public static void orderPrint(int n) {
        if (n / 10 == 0) {
            System.out.print(n % 10 + " ");
            return;
        }
        orderPrint(n / 10);
        System.out.print(n % 10 + " ");
    }

    public static void main3(String[] args) {
        orderPrint(1234);
    }
}
