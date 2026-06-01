package mode.factory;

public class Test {
    public static void main(String[] args) {
        User student = UserFactory.createStudent(18, "小明");
        User teacher = UserFactory.createTeacher(38, "张美丽");

        student.eat();
        teacher.eat();
    }
}
