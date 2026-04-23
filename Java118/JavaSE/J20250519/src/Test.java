public class Test {
    public static String myToString(int[] arr) {
        // [1, 2]
        StringBuilder ret = new StringBuilder("[");
        for (int i = 0; i < arr.length - 1; i++) {
            ret.append(arr[i]).append(", ");
        }
        ret.append(arr[arr.length - 1]).append("]");
        return ret.toString();
    }

    public static void main(String[] args) {
        System.out.println(myToString(new int[]{1, 2, 3, 5, 1}));
    }

    public static void fuc1(int i) {
        System.out.println("fuc1 " + i);
    }

    public static void main1(String[] args) {
        System.out.println("test");
        for (int i = 0; i < 40; i++) {
            if (i == 30) {
                System.out.println("i 是 30");
            }
        }

        System.out.println(123);
        fuc1(3);
    }
}
