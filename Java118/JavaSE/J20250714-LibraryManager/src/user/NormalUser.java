package user;

public class NormalUser extends User {
    public NormalUser(Integer userId, String name, String role) {
        super(userId, name, role);
    }


    public NormalUser(Integer userId, String name) {
        super(userId, name, "普通用户"); // 一般放在 constants 里面，而不是硬编码
    }


    private void loadBorrowedBook() {

    }

    private void storeBorrowedBook() {
    }

    @Override
    public int display() {
        System.out.println("普通⽤⼾ " + name + " 的操作菜单:");
        System.out.println("1. 查找图书");
        System.out.println("2. 打印所有的图书");
        System.out.println("3. 退出系统");
        System.out.println("4. 借阅图书");
        System.out.println("5. 归还图书");
        System.out.println("6. 查看当前个⼈借阅情况");
        System.out.println("请选择你的操作：");
        return scanner.nextInt();
    }

    //借阅图书
    public void borrowBook() {
    }

    //归还图书
    public void returnBook() {
    }

    //查看个人借阅情况
    public void viewBorrowBooks() {
    }
}
