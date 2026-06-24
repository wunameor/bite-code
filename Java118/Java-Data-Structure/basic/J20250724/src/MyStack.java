import java.util.Arrays;

public class MyStack {
    private int[] elems;
    private int useSize;

    public MyStack(int[] elems) {
        this.elems = elems;
        this.useSize = elems.length;
    }

    public MyStack() {
        this.elems = new int[1];
    }


    public void push(int elem) {
        if (useSize == elems.length) {
            elems = Arrays.copyOf(elems, elems.length * 2);
        }
        elems[useSize++] = elem;
    }

    private void checkIsEmpty() {
        if (empty()) {
            throw new RuntimeException("栈里面没有元素");
        }
    }

    public int pop() {
        checkIsEmpty();
        int ret = elems[useSize - 1];
//        elems[useSize - 1] = null
        useSize--;
        return ret;
    }

    public int peek() {
        checkIsEmpty();
        return elems[useSize - 1];
    }

    public int size() {
        return useSize;
    }

    public boolean empty() {
        return useSize == 0;
    }
}
