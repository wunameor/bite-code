import java.util.Objects;

public class HashBuck {
    static class Node {
        public int key;
        public int val;
        public Node next;

        public Node(int key, int val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return key == node.key;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key);
        }
    }

    private Node[] elems;
    private int useSize;

    private double loadFactor = 0.75;

    public HashBuck() {
        this.elems = new Node[10];
    }

    public void put(int key, int val) {
        Node node = new Node(key, val);
        put(node);

        // 检查负载因子
        if (doFactorLoad() > loadFactor) {
            // 扩容
            grow();
        }
    }

    private void grow() {
        Node[] oldElems = this.elems;
        this.elems = new Node[oldElems.length * 2];
        this.useSize = 0; // 清 0
        // 一路添加回去
        for (int i = 0; i < oldElems.length; i++) {
            Node cur = oldElems[i];
            while (cur != null) {
                put(cur);
                cur = cur.next;
            }
        }
    }

    private double doFactorLoad() {
        return useSize * 1.0 / elems.length;
    }

    // 仅仅用来添加
    private void put(Node node) {
        // 一定要添加这一个，在扩容的情况下，防止影响到其他节点
        node = new Node(node.key, node.val);
        int index = node.key % elems.length;
        // 采用头插法
        Node cur = elems[index];
        // 判断是否有相同的 key-val
        while (cur != null) {
            if (cur.equals(node)) {
                // 更新值
                cur.val = node.val;
                break;
            }
            cur = cur.next;
        }

        // 没找到相同的 key，那么就添加
        node.next = elems[index];
        elems[index] = node;
        this.useSize++;
    }

    public int get(int key) {
        int index = key % elems.length;

        Node cur = elems[index];
        while (cur != null) {
            if (cur.key == key) {
                return cur.val;
            }
            cur = cur.next;
        }
        return -1;
    }
}
