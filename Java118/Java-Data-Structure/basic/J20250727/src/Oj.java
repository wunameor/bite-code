import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Oj {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }
    class BuildTree {
        public TreeNode buildTree(int[] preorder, int[] inorder) {
            ArrayList<Integer> preorderList = createList(preorder);
            ArrayList<Integer> inorderList = createList(inorder);
            return buildTree(preorderList, inorderList);

        }

        public TreeNode buildTree(List<Integer> preorderList, List<Integer> inorderList) {
            if (preorderList.isEmpty() || inorderList.isEmpty()) {
                return null;
            }
            Integer rootV = preorderList.get(0);
            TreeNode root = new TreeNode(rootV);

            int rootIndex = inorderList.indexOf(rootV);

            int leftTreeSize = rootIndex - 1 - 0 + 1; // 两下标相减 + 1 即为个数

            root.left = buildTree(preorderList.subList(1, leftTreeSize + 1),
                    inorderList.subList(0, rootIndex));

            root.right = buildTree(preorderList.subList(leftTreeSize + 1, preorderList.size()),
                    inorderList.subList(rootIndex + 1, inorderList.size()));

            return root;
        }

        private ArrayList<Integer> createList(int[] array) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int num : array) {
                list.add(num);
            }
            return list;
        }

    }


    // https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-tree/
    class LowestCommonAncestor {



        public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
            LinkedList<TreeNode> pList = getList(root, p);
            LinkedList<TreeNode> qList = getList(root, q);

            // 此时就可以转化为获取两个链表的公共节点 从尾部开始那就没问题
            if (pList == null || qList == null) {
                return null;
            }
            TreeNode tmp;
            while (!pList.isEmpty() || !qList.isEmpty()) {
                if ((tmp = pList.removeLast()) == qList.removeLast()) {
                    return tmp;
                }
            }

            return null;
        }

        private LinkedList<TreeNode> getList(TreeNode root, TreeNode val) {
            // 不考虑 val = null 的情况
            if (root == null) {
                return null;
            }

            if (root == val) {
                LinkedList<TreeNode> ret = new LinkedList<>();
                ret.add(root);
                return ret;
            }
            // root != val
            LinkedList<TreeNode> leftList = getList(root.left, val);
            if (leftList != null) {
                leftList.add(root);
                return leftList;
            }

            LinkedList<TreeNode> rightList = getList(root.right, val);
            if (rightList != null) {
                rightList.add(root);
                return rightList;
            }

            // 没找到
            return null;
        }
    }
}
