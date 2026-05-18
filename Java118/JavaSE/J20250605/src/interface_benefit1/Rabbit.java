package interface_benefit1;

public class Rabbit extends Animal implements IRunable, ISwimable{
    public Rabbit(int age, String name) {
        super(age, name);
    }

    @Override
    public void eat() {
        System.out.println(this.name + " 吃萝卜");
    }

    @Override
    public void running() {
        System.out.println(this.name + " 逃跑");
    }

    @Override
    public void swimming() {
        System.out.println(this.name + " 游泳");
    }
}
