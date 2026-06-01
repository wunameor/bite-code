package mode.single;

public class Test {

    public static void main(String[] args) {
        SingleHungry singleHungry1 = SingleHungry.getInstance();
        SingleHungry singleHungry2 = SingleHungry.getInstance();
        System.out.println(singleHungry1 == singleHungry2);
    }

    public static void main1(String[] args) {
        SingleLazy singleLazy1 = SingleLazy.getInstance();
        SingleLazy singleLazy2 = SingleLazy.getInstance();
        System.out.println(singleLazy1 == singleLazy2);
    }
}
