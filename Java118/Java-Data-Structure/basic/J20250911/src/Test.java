public class Test {

    public static void main(String[] args) {
        HashBuck hashBuck = new HashBuck();
        hashBuck.put(1, 111);
        hashBuck.put(3, 111);
        hashBuck.put(5, 111);
        hashBuck.put(9, 111);
        hashBuck.put(13, 111);
        hashBuck.put(21, 1111);
        hashBuck.put(2, 111);
        hashBuck.put(7, 111);

        System.out.println(hashBuck.get(21));
        hashBuck.put(21, 999);
        System.out.println(hashBuck.get(21));
        System.out.println(hashBuck.get(221));
        hashBuck.put(221, 9929);
        System.out.println(hashBuck.get(221));
    }


    public static void main1(String[] args) {
        MyBinarySearchTree tree = new MyBinarySearchTree();
        int[] array = {4, 3, 2,1, 5,9,3};
        for (int i = 0; i < array.length; i++) {
            tree.insert(array[i]);
        }

        int val = 3;
        System.out.println("tree.search("+val+") " + tree.search(val));
        val = 4;
        System.out.println("tree.search("+val+") " + tree.search(val));
        val = 9;
        System.out.println("tree.search("+val+") " + tree.search(val));
        val = 0;
        System.out.println("tree.search("+val+") " + tree.search(val));
        System.out.println("===================");
        tree.remove(3);
        tree.remove(4);
        tree.remove(9);
        tree.remove(0);
        val = 3;
        System.out.println("tree.search("+val+") " + tree.search(val));
        val = 4;
        System.out.println("tree.search("+val+") " + tree.search(val));
        val = 9;
        System.out.println("tree.search("+val+") " + tree.search(val));
        val = 0;
        System.out.println("tree.search("+val+") " + tree.search(val));
    }
}
