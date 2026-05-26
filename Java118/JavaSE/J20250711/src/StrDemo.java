import java.util.Arrays;

public class StrDemo {
    public static void main(String[] args) {
        char[] ch = {'a', 'b', 'c'};
        String sch = new String(ch);
        System.out.println(Arrays.toString(ch));
        System.out.println(sch);
        System.out.println("===================");
        ch[0] = 'k';
        System.out.println(Arrays.toString(ch));
        System.out.println(sch);
    }
}
