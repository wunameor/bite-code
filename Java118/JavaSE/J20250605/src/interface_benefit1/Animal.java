package interface_benefit1;

public abstract class Animal {
    public int age;
    public String name;

    public Animal(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public abstract void eat();
}
