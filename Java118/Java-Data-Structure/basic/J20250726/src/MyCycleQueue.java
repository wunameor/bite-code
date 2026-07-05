public class MyCycleQueue {


    private int[] elems;
    private int front; // 头节点下标
    private int rear; // 尾节点下标
    private int useSize;

    public MyCycleQueue() {
        this.elems = new int[5];
    }

    public MyCycleQueue(int length) {
        this.elems = new int[length];
    }

    private int getIndex(int start, int offset) {
        //  加上 this.elems.length 是考虑到 offset 为负数的情况
        return (start + offset + this.elems.length) % this.elems.length;
    }


    public void offer(int val) {
        if (isFull()) {
            throw new RuntimeException("元素已满");
        }

        elems[rear] = val;
        rear = getIndex(rear, 1);

        useSize++;
    }

    private boolean isFull() {
        return this.elems.length == useSize;
    }

    public int poll() {
        checkIsEmpty();
        int tmp = elems[front];
        // elems[front] = null;
        front = getIndex(front, 1);
        useSize--;
        return tmp;
    }

    private void checkIsEmpty() {
        // 由于有 useSize 所以使用 useSize 判断
        // 如果没有 useSize 那么就用 rear == getIndex(front, 1) 或者 front == getIndex(rear, -1) 来判断
        if (isEmpty()) {
            throw new NullPointerException("没有元素");
        }
    }

    public int peek() {
        checkIsEmpty();
        return elems[front];
    }

    public int size() {
        return useSize;
    }

    public boolean isEmpty() {
        return useSize == 0;
    }
}
