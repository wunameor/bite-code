import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        int[] array = {111, 121,0,12,5, 111};
//        Sort.insertSort(array);
        Sort.quickSortNor(array);
        System.out.println(Arrays.toString(array));
    }
}
