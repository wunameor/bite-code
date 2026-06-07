
class MyArray {
    private Object[] array = new Object[5];

    public void setValue(int index, Object val) {
        array[index] = val;
    }

    public Object getValue(int index) {
        return array[index];
    }
}

public class Test {
    public static void main(String[] args) {
        MyArray myArray = new MyArray();
        myArray.setValue(1, "test1");
        myArray.setValue(2, 20);

        System.out.println((String) myArray.getValue(1));
        System.out.println((Integer) myArray.getValue(2));
    }
}
