import java.util.LinkedList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        List<Integer> list1 = list.subList(1, -1);
        System.out.println(list1.isEmpty());
    }


    public static void main1(String[] args) {
        BinaryTree tree = new BinaryTree();

        /*
        * 结构：
        * A
        * B     C
        * D     E   F
        */
        tree.preOrder();
        tree.inOrder();
        tree.postOrder();


        System.out.println("size: " + tree.size());
        System.out.println("getLeafNodeCount: " + tree.getLeafNodeCount());

        int k = 2;
        System.out.println("getKLevelNodeCount: " + tree.getKLevelNodeCount(k) + " k = " + k);
        k = 3;
        System.out.println("getKLevelNodeCount: " + tree.getKLevelNodeCount(k) + " k = " + k);
        k = 4;
        System.out.println("getKLevelNodeCount: " + tree.getKLevelNodeCount(k) + " k = " + k);
        System.out.println("getHeight: " + tree.getHeight());


        char val = 'C';
        System.out.println("find: " + tree.find(val) + " val = " + val);
        val = 'B';
        System.out.println("find: left " + tree.find(val).left + " val = " + val);
        val = 'F';
        System.out.println("find: right " + tree.find(val).right + " val = " + val);
        val = 'K';
        System.out.println("find: " + tree.find(val) + " val = " + val);

        System.out.print("levelOrder: ");
        tree.levelOrder();


        System.out.println("isCompleteTree: " + tree.isCompleteTree());
        System.out.println("isCompleteTree: createTree(): " + tree.isCompleteTree(createTree())); // 不符合封装性，仅仅用于测试
    }

    private static BinaryTree.TreeNode createTree() {
        BinaryTree.TreeNode A = new BinaryTree.TreeNode('A');
        BinaryTree.TreeNode B = new BinaryTree.TreeNode('B');
        BinaryTree.TreeNode C = new BinaryTree.TreeNode('C');
        BinaryTree.TreeNode D = new BinaryTree.TreeNode('D');
        BinaryTree.TreeNode E = new BinaryTree.TreeNode('E');
        BinaryTree.TreeNode F = new BinaryTree.TreeNode('F');


//        A.left = B;
//        A.right = C;
//        B.left = D;
//        B.right = F;
//        C.left = E;

        return A;
    }
}
