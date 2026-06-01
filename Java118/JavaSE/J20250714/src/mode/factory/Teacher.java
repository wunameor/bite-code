package mode.factory;

public class Teacher extends User {
    public Teacher(int age, String name) {
        super(age, name);
    }

    @Override
    public void eat() {
        System.out.println("teacher " + this.name + " eat...");
    }
}
