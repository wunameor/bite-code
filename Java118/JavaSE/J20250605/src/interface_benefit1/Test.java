package interface_benefit1;

public class Test {
    public static void swimming(ISwimable swimable) {
        swimable.swimming();
    }

    public static void flying(IFlyable flyable) {
        flyable.flying();
    }

    public static void running(IRunable runable) {
        runable.running();
    }

    public static void main(String[] args) {
        Cat cat = new Cat(12,"汤姆");
        Finish finish = new Finish(2,"肥波");
        Rabbit rabbit = new Rabbit(1,"小白");

        cat.eat();
        running(cat);
        flying(cat);
        System.out.println("--------------");
        finish.eat();
        swimming(finish);
        System.out.println("--------------");
        rabbit.eat();
        running(rabbit);
        swimming(rabbit);
    }
}
