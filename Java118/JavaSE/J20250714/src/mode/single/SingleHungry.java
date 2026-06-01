package mode.single;

public class SingleHungry {
    private static SingleHungry singleHungry = new SingleHungry();

    private SingleHungry() {

    }

    public static SingleHungry getInstance() {
        return singleHungry;
    }
}
