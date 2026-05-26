import java.util.Arrays;

public class Test {
    public int firstUniqChar(String s) {
        int[] arr = new int[26];
        Arrays.fill(arr, -1);
        for(int i = 0; i < s.length(); i++) {
            int index  = s.charAt(i) - 'a';
            int tmp = arr[index];
            if (tmp == -1) {
                arr[index] = i;
            } else {
                arr[index] = -2;
            }
        }
        int min = s.length();
        for(int i = 0; i < arr.length; i++) {
            if (arr[i] >= 0) {
                min = Math.min(min, arr[i]);
            }
        }
        return min == s.length() ? -1 : min;
    }
}
