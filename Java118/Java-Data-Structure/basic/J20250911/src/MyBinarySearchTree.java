public class MyBinarySearchTree {
    static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    private TreeNode root;

    private TreeNode[] searchChildAndParent(int val) {
        return searchChildAndParent(val, this.root);
    }

    /**
     * 寻找子节点及其对应的父节点,
     * @return 数组第一个是子节点，第二个是父节点.
     * 如果没找到返回 那么子节点为 null
     * 如果是根节点，那么父亲节点是 null
     */
    private TreeNode[] searchChildAndParent(int val, TreeNode root) {
        TreeNode[] treeNodes = new TreeNode[2];

        TreeNode cur = root, parent = null;

        while (cur != null) {
            if (cur.val == val) {
                treeNodes[0] = cur;
                treeNodes[1] = parent;
                break;
            }
            // 说明一定不相等
            parent = cur;
            cur = val < cur.val ? cur.left : cur.right;
        }

        treeNodes[0] = cur;
        treeNodes[1] = parent;
        return treeNodes;
    }

    public boolean search(int val) {
        TreeNode[] treeNodes = searchChildAndParent(val);

        // 找到了
        return treeNodes[0] != null;
    }


    public void insert(int val) {
        TreeNode node = new TreeNode(val);
        if (this.root == null) {
            root = node;
            return;
        }

        TreeNode[] treeNodes = searchChildAndParent(val);
        TreeNode child = treeNodes[0], parent = treeNodes[1];

        if (child != null) {
            // 说明找到了 那么就不添加
            return;
        }

        if (val < parent.val) {
            parent.left = node;
        } else if (val > parent.val) {
            parent.right = node;
        } else {
            return;
        }
    }


    public void remove(int val) {
        TreeNode[] treeNodes = searchChildAndParent(val);
        TreeNode child = treeNodes[0], parent = treeNodes[1];

        // 没找到子节点
        if (child == null) {
            return;
        }

        if (child.left == null) {
            if (child == this.root) {
                this.root = child.right;
            } else {
                if (parent.left == child) {
                    parent.left = child.right;
                } else {
                    // parent.right == child
                    parent.right = child.right;
                }
            }
        } else if (child.right == null) {
            if (child == this.root) {
//                this.root.left = child.left;
                this.root = child.left;
            } else {
                // 这里必须添加 判断 child 是左子树还是右子树
                if (parent.left == child) {
                    parent.left = child.left;
                } else {
                    // parent.right == child
                    parent.right = child.left;
                }
            }
        } else {
            // 左右都不为空
            removeNode(child, parent);
        }
    }

    /**
     * 删除的节点有左右两颗子树
     */
    private void removeNode(TreeNode child, TreeNode parent) {
        // 通过替换的方法来删除

        // 一直往左走，找到 child 右子树的最小值
        TreeNode tmp = child.right, tmpParent = child;
        while (tmp.left != null) {
            tmpParent = tmp;
            tmp = tmp.left;
        }
        // 此时tmp 是最小
        if (tmpParent == child) {
            // 说明右子树只有一个节点
            child.val = tmp.val;
            child.right = tmp.right;
        } else {
            tmpParent.left = tmp.right;
            child.val = tmp.val;
        }
    }
}
