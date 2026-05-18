package interface_benefit1;

public class Finish extends Animal implements ISwimable{
    public Finish(int age, String name) {
        super(age, name);
    }

    @Override
    public void eat() {
        System.out.println(this.name + " 吃鱼粮");
    }

    @Override
    public void swimming() {
        System.out.println(this.name + " 游泳");
    }
}
