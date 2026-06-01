package mode.factory;

public abstract class User {
    public int age;
    public String name;

    public User(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public abstract void eat();
}
