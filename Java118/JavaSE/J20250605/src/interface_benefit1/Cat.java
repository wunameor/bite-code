package interface_benefit1;

public class Cat extends Animal implements IFlyable, IRunable {
    public Cat(int age, String name) {
        super(age, name);
    }

    @Override
    public void eat() {
        System.out.println(this.name + " 吃猫粮");
    }

    @Override
    public void flying() {
        System.out.println(this.name + " 翱翔天空（猫和老鼠）");
    }

    @Override
    public void running() {
        System.out.println(this.name + " 四条腿跑");
    }
}
