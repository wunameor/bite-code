public class MyQueue {
    static class Node {
        int val;
        Node next;
        Node prev;

        public Node() {
        }

        public Node(int val) {
            this.val = val;
        }
    }

    private Node head;
    private Node last;
    private int useSize;

    public MyQueue() {
    }


    public void offer(int val) {
        Node node = new Node(val);
        if (isEmpty()) {
            last = head = node;
            useSize++;
            return;
        }

        last.next = node;
        node.prev = last;
        last = node;
        useSize++;
    }

    public int poll() {
        checkIsEmpty();
        Node tmp = head;
        if (useSize == 1) {
            head = last = null;
            useSize--;
            return tmp.val;
        }
        head = head.next;
        head.prev = null;
        tmp.next = null;
        useSize--;
        return tmp.val;
    }

    private void checkIsEmpty() {
        if (isEmpty()) {
            throw new NullPointerException("没有元素");
        }
    }

    public int peek() {
        checkIsEmpty();
        return head.val;
    }

    public int size() {
        return useSize;
    }

    public boolean isEmpty() {
        return useSize == 0;
    }
}
