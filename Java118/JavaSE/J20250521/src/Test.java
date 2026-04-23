import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        int[] arr = {31,125,1,-3, 12,7};
        buddleSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    public static void swap(int[] arr, int i, int j) {
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[i] ^ arr[j];
    }
    public static void buddleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    // [1, 2, 3, 4, 5]
    public static int binarySearch(int[] arr, int key) {
        int left = 0, right = arr.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int temp = arr[mid];
            if (temp > key) {
                right = mid - 1;
            } else if (temp == key) {
                return mid;
            } else { // temp < key
                left = mid + 1;
            }
        }
        return -1;
    }

    public static void main1(String[] args) {
        int[] arr = {1, 1, 3, 4};
        System.out.println("index = " + binarySearch(arr, 4));
    }
}
