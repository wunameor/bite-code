package mode.factory;

public class UserFactory {
    public static User createStudent(int age, String name) {
        return new Student(age, name);
    }

    public static User createTeacher(int age, String name) {
        return new Teacher(age, name);
    }
}
