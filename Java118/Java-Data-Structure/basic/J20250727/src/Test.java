public class Test {
    public static void main(String[] args) {
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

    }
}
