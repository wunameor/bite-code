import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        int[] array = {1, 3,2,1,3,5,1,2,6,8,2,6,4,7,3,5};
//        Sort.insertSort(array);
        Sort.countSort(array);
        System.out.println(Arrays.toString(array));
    }
}
