import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {

    // 其他测试
    public static void main(String[] args) {

        MySingleLinkedList list = new MySingleLinkedList(new int[]{1,1,2,1,3,4,1});
        System.out.println("useSize = " + list.size());
        System.out.println("contains 1 = " + list.contains(1));
        System.out.println("contains 2 = " + list.contains(2));
        System.out.println("contains 12 = " + list.contains(12));
        list.display();
        System.out.println("---------");

        list.removeAllKey(1);
        list.addLast(12);
        System.out.println("useSize = " + list.size());
        System.out.println("contains 1 = " + list.contains(1));
        System.out.println("contains 2 = " + list.contains(2));
        System.out.println("contains 12 = " + list.contains(12));
        list.display();
        System.out.println("---------");
    }

    // 删除测试
    public static void main4(String[] args) {
        MySingleLinkedList list = new MySingleLinkedList(new int[]{1,1,2,1,3,4,1});
        list.display();
        System.out.println("---------");


        list.remove(1);
//        list.remove(1);
//        list.remove(1);
//        list.remove(1);
//        list.remove(1);
//        list.remove(1);
//        list.removeAllKey(1);
        System.out.println("useSize = " + list.size());
        list.display();
        System.out.println("---------");


        list.clear();
        System.out.println("useSize = " + list.size());
        list.display();
    }

    // 添加测试
    public static void main3(String[] args) {
        MySingleLinkedList list = new MySingleLinkedList();
//        list.addIndex(1, 111);
        list.addIndex(0, 111);
        list.addIndex(1, 222);
        list.addIndex(2, 333);
        list.addIndex(2, 444);
        list.addIndex(1, 555);
        list.addIndex(1, 666);

//        list.addFirst(1);
//        list.addFirst(2);
//        list.addFirst(3);

//        list.addLast(1);
//        list.addLast(2);
//        list.addLast(4);

        list.display();
    }

    public static void main2(String[] args) {
        MySingleLinkedList list = new MySingleLinkedList(new int[]{});
//        MySingleLinkedList list = new MySingleLinkedList();
        list.display();
    }


    public static void main1(String[] args) {
        List<Integer> list = new ArrayList<>();

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
    }
}
