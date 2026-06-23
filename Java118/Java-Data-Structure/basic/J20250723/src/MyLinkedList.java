public class MyLinkedList {
    private ListNode head;
    private ListNode last;
    private int useSize;

    static class ListNode {
        public int val;
        public ListNode next;
        public ListNode prev;

        public ListNode() {
        }

        public ListNode(int val) {
            this.val = val;
        }
    }

    public MyLinkedList() {
    }

    public MyLinkedList(int[] arrays) {
        ListNode head = new ListNode(-1);
        ListNode cur = head, prev = head;
        for (int num : arrays) {
            cur.next = new ListNode(num);
            cur = cur.next;
            cur.prev = prev;
            prev = cur;
        }
        this.last = cur;
        this.head = head.next;
        this.head.prev = null;
        this.useSize = arrays.length;
    }

    //头插法
    public void addFirst(int data) {
        addIndex(0, data);
    }

    //尾插法
    public void addLast(int data) {
        addIndex(useSize, data);
    }

    //任意位置插入，第一个数据节点为0号下标
    public void addIndex(int index, int data) {
        if (index < 0 || index > useSize) {
            System.out.println("下标异常 预期 0 ~ " + useSize + " 实际：" + index);
            return;
        }

        if (useSize == 0) {
            last = new ListNode(data);
            head = last;
            useSize++;
            return;
        }

        ListNode newNode = new ListNode(data);
        if (index == 0) {
            // 头插
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
            useSize++;
            return;
        }

        if (index == useSize) {
            // 尾插
            newNode.prev = last;
            last.next = newNode;
            last = newNode;
            useSize++;
            return;
        }

        ListNode tmp = head;
        while (index != 0) {
            index--;
            tmp = tmp.next;
        }

        // tmp 就是要插入的下标
        newNode.prev = tmp.prev;
        newNode.next = tmp;
        tmp.prev.next = newNode;
        tmp.prev = newNode;
        useSize++;
    }

    private ListNode getByVal(ListNode head, int key) {
        ListNode tmp = head;
        while (tmp != null) {
            if (tmp.val == key) {
                return tmp;
            }
            tmp = tmp.next;
        }
        return null;
    }

    private ListNode getByVal(int key) {
        return getByVal(this.head, key);
    }

    //查找是否包含关键字key是否在单链表当中
    public boolean contains(int key) {
        return getByVal(key) != null;
    }

    //删除第一次出现关键字为key的节点
    public void remove(int key) {
        remove(getByVal(key));
    }

    private void remove(ListNode delNode) {
        if (delNode == null) {
            return;
        }

        ListNode prev = delNode.prev, next = delNode.next;
        if (delNode == head) {
            this.head = head.next;
            this.head.prev = null;
            useSize--;
            return;
        }

        if (delNode == last) {
            this.last = last.prev;
            this.last.next = null;
            useSize--;
            return;
        }

        prev.next = next;
        next.prev = prev;
        useSize--;
    }

    //删除所有值为key的节点
    public void removeAllKey(int key) {
        ListNode head = this.head;
        while(true) {
            ListNode delNode = getByVal(head, key);
            if (delNode == null) {
                return;
            }
            if (head == delNode) {
                // 删除头节点，那就使用下一个
                head = delNode.next;
            } else {
                // 获取到要删除的上一个节点
                head = delNode.prev;
            }
            remove(delNode);
        }
    }

    //得到单链表的长度
    public int size() {
        return useSize;
    }

    public void display() {
        ListNode tmp = head;
        while (tmp != null) {
            System.out.print(tmp.val + " ");
            tmp = tmp.next;
        }
        System.out.println();
    }

    public void clear() {
//        useSize = 0;
//        last = null;
//        head = null;
        if (head == null) {
            return;
        }
        ListNode cur = head;
        while (cur != null) {
            ListNode next = head.next;
            cur.next = null;
            cur.prev = null;
//            cur.val = null;
            cur = next;
        }
        head = null;
        last = null;
        useSize = 0;
    }
}
