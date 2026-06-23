public class Test {

    public static void main(String[] args) {
        MyLinkedList list = new MyLinkedList(new int[]{2,1,1,1,1111,2,2,3,3,1,2});
//        MyLinkedList list = new MyLinkedList(new int[]{2,3,1111,2,2,1,2});
        list.display();
        System.out.println("useSize = " + list.size());
        System.out.println("============");
        list.remove(1111);
        list.remove(1111);
        list.display();
        System.out.println("useSize = " + list.size());
        System.out.println("============");

        System.out.println(list.contains(2));
        list.removeAllKey(2);
        System.out.println(list.contains(2));
        list.display();
        System.out.println("useSize = " + list.size());
        System.out.println("============");
        list.clear();
        list.display();
        System.out.println("useSize = " + list.size());
    }


    public static void main1(String[] args) {
        MyLinkedList list = new MyLinkedList();
        list.display();
        System.out.println("============");
        list.addFirst(1);
        list.addFirst(2);
        list.addFirst(3);
        list.display();
        System.out.println("============");
        list.addLast(90);
        list.addLast(80);
        list.addLast(70);
        list.display();
        System.out.println("============");
        list.addIndex(1,200);
        list.addIndex(list.size() - 1, 300);
        list.display();
    }
}
