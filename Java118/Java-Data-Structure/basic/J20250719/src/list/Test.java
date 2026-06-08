package list;

public class Test {
    public static void main(String[] args) {
        MyArrayList myArrayList = new MyArrayList();
        myArrayList.add(2);
        myArrayList.add(3);
        myArrayList.add(4);
        myArrayList.add(5);
        myArrayList.add(6);
        myArrayList.add(5, 199);

        myArrayList.display();
        System.out.println("-----------");

        myArrayList.remove(3);
        myArrayList.remove(3);
        myArrayList.removeByPos(0);
        myArrayList.display();

        System.out.println("-----------");

//        int index = myArrayList.indexOf(1);
        int index = myArrayList.indexOf(6);
        System.out.println("index = " + index + " value = " + myArrayList.get(index));
        boolean contains = myArrayList.contains(1);
//        boolean contains = myArrayList.contains(6);
        System.out.println("contain: " + contains);

        myArrayList.set(0, 222);
        myArrayList.set(myArrayList.size() - 1, 121);
        myArrayList.display();

        System.out.println("-----------");
        myArrayList.clear();
        myArrayList.add(999);
        myArrayList.display();


    }
}
