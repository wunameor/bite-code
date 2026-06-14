public class MySingleLinkedList {
    static class Node {
        public int val;
        public Node next;

        public Node(int val, Node next) {
            this.val = val;
            this.next = next;
        }

        public Node(int val) {
            this.val = val;
        }

        public Node() {
        }
    }

    private Node head;
    private int useSize;

    public MySingleLinkedList(int[] array) {
        if (array.length == 0) {
            return;
        }
        head = new Node();
        Node tmp = head;

        for (int i = 0; i < array.length; i++) {
            tmp.val = array[i];
            if (i != array.length - 1) {
                tmp.next = new Node();
                tmp = tmp.next;
            }
        }
        useSize = array.length;
    }

    public MySingleLinkedList() {
//        head = new Node();
    }

    //头插法
    public void addFirst(int data){
        addIndex(0, data);
    }
    //尾插法
    public void addLast(int data) {
        addIndex(useSize, data);
    }
    //任意位置插⼊,第⼀个数据节点为0号下标
    public void addIndex(int index,int data){
        if (index > useSize || index < 0) {
            System.out.println("index 下标不合法： index = " + index + " 合法下标为 0 ~ " + useSize);
            return;
        }

        Node newNode = new Node(data);
        if (index == 0) {
            // 头插
            newNode.next = head;
            head = newNode;
            useSize++;
            return;
        }

        // 不可能为空，因为下标合法，而且不是头插， index >= 1 ，至少有两个数
//        if (isEmpty()) {
//            head = newNode;
//            return;
//        }


        Node tmp = head;
        Node pre = null;
        int curIndex = 0;
        while (tmp.next != null && index != curIndex) {
            pre = tmp;
            tmp = tmp.next;
            curIndex++;
        }

        // 必须添加这个判断，不然 pre 可能会空指针异常（只有一个数的时候尾插）
        if (index == useSize) {
            // 尾插
            tmp.next = newNode;
            useSize++;
            return;
        }


        newNode.next = tmp;
        pre.next = newNode;
        useSize++;


    }
    //查找是否包含关键字key是否在单链表当中
    public boolean contains(int key){
        Node tmp = head;
        while (tmp != null) {
            if (tmp.val == key) {
                return true;
            }
            tmp = tmp.next;
        }
        return false;
    }
    //删除第⼀次出现关键字为key的节点
    public void remove(int key){
        removeKeyByCount(key, 1);
    }
    //删除所有值为key的节点
    public void removeAllKey(int key){
        removeKeyByCount(key, useSize);
    }

    /**
     * 移除 count 个 key
     * @param key
     * @param count
     */
    private void removeKeyByCount(int key, int count) {
        Node tmp = head;
        Node pre = null;
        while (tmp != null && count > 0) {
            if (tmp.val == key) {
                // 移除
                if (pre == null) {
                    // 头节点
                    tmp = tmp.next;
                    head = head.next;
                } else {
                    // 中间的节点
                    pre.next = tmp.next;
                    tmp = tmp.next;
                }
                useSize--;
                count--;
            } else {
                pre = tmp;
                tmp = tmp.next;
            }
        }
    }
    //得到单链表的⻓度
    public int size(){
        return useSize;
    }
    public void clear() {
        head = null;
        useSize = 0;
    }

    public boolean isEmpty() {
        return head == null;
    }
    public void display() {
        if (isEmpty()) {
            System.out.println("null");
            return;
        }

        Node tmp = head;
        while (tmp != null) {
            System.out.print(tmp.val + " ");
            tmp = tmp.next;
        }
        System.out.println();
    }
}
