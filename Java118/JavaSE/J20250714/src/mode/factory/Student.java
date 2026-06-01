package mode.factory;

public class Student extends User{
    public Student(int age, String name) {
        super(age, name);
    }

    @Override
    public void eat() {
        System.out.println("student " + this.name + " eat...");
    }
}
