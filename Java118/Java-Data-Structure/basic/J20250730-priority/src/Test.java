public class Test {
    public static void main(String[] args) {
        MyPriorityTree tree = new MyPriorityTree(new int[]{3,2,17,32,1,0});
        tree.display();
        tree.offer(19);
        tree.display();
        System.out.println(tree.poll());
        tree.display();
        tree.sort();
        tree.display();
    }
}
