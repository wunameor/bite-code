package string;

public class Demo {
    public static void main(String[] args) {
        String str1 = "hello";
        String str2 = "hello";
        System.out.println(str1 == str2);
        System.out.println("================");
        String str3 = new String("hello");
        String str4 = new String("hello");
        System.out.println(str1 == str3);
        System.out.println(str4 == str3);
        System.out.println(str3.equals(str4));
        System.out.println("================");
        String str5 = "HELLO";

        System.out.println(str1.compareToIgnoreCase(str5));
    }
}
