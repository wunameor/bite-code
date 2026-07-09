import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        int[] array = {21, 42, 31, 4, 25, 6, 111};
//        Sort.insertSort(array);
        Sort.heapSort(array);
        System.out.println(Arrays.toString(array));
    }
}
