import java.util.Arrays;

public class MyPriorityTree {
    private int[] elems;
    private int useSize;

    public MyPriorityTree() {
    }

    public MyPriorityTree(int[] elems) {
        init(elems);
        createHeap();
    }

    public void init(int[] array) {
        this.elems = Arrays.copyOf(array, array.length * 2);
        this.useSize = array.length;
    }

    public void createHeap() {
        for (int parent = (useSize - 1 - 1) / 2; parent >= 0; parent--) {
            // 从最后一颗树开始
            siftDown(parent, useSize);
        }
    }

    private void siftDown(int parent, int end) {
        int child = parent * 2 + 1;
        while (child < end) {
            if (child + 1 < end && elems[child] < elems[child + 1]) {
                child++;
            }
            // 此时child 一定指向较大值
            if (elems[child] > elems[parent]) {
                swap(child, parent);
                parent = child;
                child = parent * 2 + 1;
            } else {
                // 说明已经是大根堆了
                return;
            }
        }
    }

    private void swap(int i, int j) {
        int tmp = elems[i];
        elems[i] = elems[j];
        elems[j] = tmp;
    }

    public void offer(int val) {
        if (isFull()) {
            expand();
        }

        // 先尾插，然后向上调整
        elems[useSize++] = val;
        siftUp(useSize - 1);
    }

    public int poll() {
        // 删除堆顶元素
        if (isEmpty()) {
            return -1;
        }
        int val = elems[0];
        swap(0, useSize - 1);
        // elems[useSize] = null;
        useSize--;
        siftDown(0, useSize);
        return val;
    }

    public int peek() {
        // 删除堆顶元素
        if (isEmpty()) {
            return -1;
        }

        return elems[0];
    }

    private void siftUp(int child) {
        while (child != 0) {
            int parent = (child - 1) / 2;
            if (elems[child] > elems[parent]) {
                // 交换
                swap(child, parent);
                child = parent;
            } else {
                return;
            }
        }
    }

    private boolean isFull() {
        return useSize == elems.length;
    }

    public boolean isEmpty() {
        return useSize == 0;
    }

    private void expand() {
        elems = Arrays.copyOf(elems, elems.length * 2);
    }

    public void sort() {
        int end = useSize - 1;
        // 注意边界即可
        while (end > 0) {
            swap(0, end);
            siftDown(0, end);
            end--;
        }
    }

    public void display() {
        for (int i = 0; i < useSize; i++) {
            System.out.print(elems[i] + " ");
        }
        System.out.println();
    }
}
