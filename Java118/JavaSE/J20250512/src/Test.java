public class Test {

    public static void main2(String[] args) {
        for (int i = 1; i <= 100; i++) {
            if (i % 3 == 0 && i % 4 == 0) {
                System.out.println(i);
            }
        }
    }

    public static void main1(String[] args) {
        int a = 3;
        switch (a) {
            case 2:
                System.out.println(2);
                break;
            case 1:
                System.out.println(1);
                break;
            default:
                System.out.println("others");
//                break;
            case 3:
                System.out.println(3);
//                break;
        }
    }
}
