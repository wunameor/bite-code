package Preparation;

class MyArray<T extends Comparable<T>> {

    public T getMax( T[] array) {
        T max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i].compareTo(max) > 0) {
                max = array[i];
            }
        }
        return max;
    }
}

class MyArrayUtil {

    public <T extends Comparable<T>> T getMax( T[] array) {
        T max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i].compareTo(max) > 0) {
                max = array[i];
            }
        }
        return max;
    }
}
public class Test {
    public static void main(String[] args) {

    }
}
