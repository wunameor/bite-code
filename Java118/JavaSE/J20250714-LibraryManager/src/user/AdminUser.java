package user;

import book.Book;
import book.Library;

import java.time.LocalDateTime;
import java.util.Scanner;

public class AdminUser extends User {

    private Scanner in = new Scanner(System.in);
    private Library library = Library.getLibrary();

    public AdminUser(Integer userId, String name, String role) {
        super(userId, name, role);
    }

    public AdminUser(Integer userId, String name) {
        super(userId, name, "管理员");
    }

    @Override
    public int display() {
        System.out.println("管理员 " + name + " 的操作菜单:");
        System.out.println("1. 查找图书");
        System.out.println("2. 打印所有的图书");
        System.out.println("3. 退出系统");
        System.out.println("4. 上架图书");
        System.out.println("5. 修改图书");
        System.out.println("6. 下架图书");
        System.out.println("7. 统计借阅次数");
        System.out.println("8. 查看最后欢迎的前K本书");
        System.out.println("9. 查看库存状态");
        System.out.println("10. 按类别统计图书 ");
        System.out.println("11. 按作者统计图书 ");
        System.out.println("12. 检查超过⼀年未下架的图书");
        System.out.println("请选择你的操作：");
        return scanner.nextInt();
    }

    //上架图书
    public void addBook() {
        System.out.println("添加书籍操作...");

        in.nextLine();
        System.out.println("请输入书名：");
        String title = in.nextLine();
        System.out.println("请输入作者：");
        String author = in.nextLine();
        System.out.println("请输入类别：");
        String category = in.nextLine();
        System.out.println("请输入出版年份：");
        int publishYear = in.nextInt();
        in.nextLine();

        Book book = new Book(title, author, category, publishYear, LocalDateTime.now());
        library.addBook(book);
    }

    //图书修改 ⽀持修改书名 作者 类别
    public void updateBook() {
    }

    //删除书籍
    public void removeBook() {
        System.out.println("移除图书...");
    }

    //统计每本书的借阅次数
    public void borrowCount() {
    }

    //查询最受欢迎的前n本书
    public void generateBook() {
    }

    //查看库存状态
    public void checkInventoryStatus() {
    }

    //按照类别 统计图书
    public void categorizeBooksByCategory() {
    }

    //按照作者统计图书
    public void categorizeBooksByAuthor() {
    }

    //并移除上架超过⼀年的图书
    public void checkAndRemoveOldBooks() {
    }

    public void exit() {
    }
}
