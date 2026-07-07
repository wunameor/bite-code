import java.util.*;

public class BinaryTree {
    public static class TreeNode {
        public char val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode() {
        }

        public TreeNode(char val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return "TreeNode{" +
                    "val=" + val +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }

    private TreeNode root;

    public BinaryTree() {
        creatTree();
    }

    public void creatTree() {
        TreeNode A = new TreeNode('A');
        TreeNode B = new TreeNode('B');
        TreeNode C = new TreeNode('C');
        TreeNode D = new TreeNode('D');
        TreeNode E = new TreeNode('E');
        TreeNode F = new TreeNode('F');

        A.left = B;
        A.right = C;
        B.left = D;
        C.left = E;
        C.right = F;

        this.root = A;
    }

    public void preOrder() {
        preOrder(this.root);
        System.out.println();
    }

    private void preOrder(TreeNode root) {
        if (root == null) {
            return;
        }

        System.out.print(root.val);
        preOrder(root.left);
        preOrder(root.right);
    }

    public void inOrder() {
        inOrder(this.root);
        System.out.println();
    }

    private void inOrder(TreeNode root) {
        if (root == null) {
            return;
        }

        inOrder(root.left);
        System.out.print(root.val);
        inOrder(root.right);
    }

    public void postOrder() {
        postOrder(this.root);
        System.out.println();
    }

    private void postOrder(TreeNode root) {
        if (root == null) {
            return;
        }

        postOrder(root.left);
        postOrder(root.right);
        System.out.print(root.val);
    }

    // 获取树中节点的个数
    private int size(TreeNode root) {
        if (root == null) {
            return 0;
        }

        return size(root.left) + size(root.right) + 1;
    }

    public int size() {
        return size(this.root);
    }
    // 获取叶⼦节点的个数
    private int getLeafNodeCount(TreeNode root) {
        if (root == null) {
            return 0;
        }
        if (root.left == null && root.right == null) {
            return 1;
        }

        return getLeafNodeCount(root.left) + getLeafNodeCount(root.right);
    }

    public int getLeafNodeCount() {
        return getLeafNodeCount(this.root);
    }

    // 获取第K层节点的个数
    public int getKLevelNodeCount(int k) {
        return getKLevelNodeCount(this.root, k, 1); // 从第一层开始
    }

    private int getKLevelNodeCount(TreeNode root,int k, int curFloor) {
        if (root == null) {
            return 0;
        }
        if (k > curFloor) {
            return getKLevelNodeCount(root.left, k, curFloor + 1) +
                    getKLevelNodeCount(root.right, k, curFloor + 1);
        }
        if (k == curFloor) {
            return 1;
        }
        // k < curFloor
        return 0;
    }

    // 获取⼆叉树的⾼度
    public int getHeight() {
        return getHeight(this.root);
    }

    private int getHeight(TreeNode root) {
        if (root == null) {
            return 0;
        }

        if (root.left == null && root.right == null) {
            return 1;
        }
        // 至少有一个不为空
        int leftHeight = getHeight(root.left);
        int rightHeight = getHeight(root.right);

        return Math.max(leftHeight, rightHeight) + 1;
    }
    // 检测值为value的元素是否存在
    public TreeNode find(char val) {
        return find(this.root, val);
    }

    // 前序遍历查找
    private TreeNode find(TreeNode root, char val) {
        if (root == null) {
            return null;
        }

        if (root.val == val) {
            return root;
        }
        TreeNode left = find(root.left, val);
        if (left != null) {
            return left;
        }

        return find(root.right, val);
    }
    //层序遍历
    public void levelOrder() {
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.add(this.root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node != null) {
                queue.add(node.left);
                queue.add(node.right);
                System.out.print(node.val);
            }
        }
        System.out.println();
    }

    // 判断⼀棵树是不是完全⼆叉树
    public boolean isCompleteTree() {
        return isCompleteTree(this.root);
    }

    public boolean isCompleteTree(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.add(root);

        // boolean isShowNull = false; // 是否出现 null
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node != null) {
                queue.add(node.left);
                queue.add(node.right);
            } else {
                // 如果后面还有不为空的，那么就返回 false
                // isShowNull = true;
                break;
            }
        }

        // 判断是否还有不是 空的节点
        if (!queue.isEmpty()) {
            while (!queue.isEmpty()) {
                if (queue.poll() != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
