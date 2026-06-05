package user;

import book.Book;
import book.Library;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
        System.out.println("10. 检查超过⼀年未下架的图书");
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

        Book book = new Book(title, author, category, publishYear, LocalDate.now());
        library.addBook(book);
    }

    //图书修改 ⽀持修改书名 作者 类别（后期的话是传入Id 经行修改的）
    public void updateBookById() {
        // 如果通过前端网页传入，一般是Book 这个对象，但是这里就不建议了，因为如果没有，需要重新搜索
        System.out.println("请输入要更新的 bookId：");
        int bookId = in.nextInt();
        Book book = library.getBookById(bookId);
        if (book == null) {
            System.out.println("未找到相关书籍： bookId = " + bookId);
            return;
        }
        System.out.println("图书的相关信息：" + book);

        in.nextLine();
        System.out.println("请输入新的书名（输入空那么就不修改）：");
        String title = in.nextLine();
        System.out.println("请输入新的作者（输入空那么就不修改）：");
        String author = in.nextLine();
        System.out.println("请输入新的类别（输入空那么就不修改）：");
        String category = in.nextLine();

        Book newBook = new Book(title, author, category, null, null);
        newBook.setBookId(bookId);
        library.updateBookById(newBook);
        System.out.println("图书的更新后信息：" + book);
    }


    //删除书籍
    public void removeBook() {
        System.out.println("请输入要删除的 bookId：");
        int bookId = in.nextInt();
        Integer index = library.getBookIndexById(bookId);
        if (index == null) {
            System.out.println("未找到相关书籍： bookId = " + bookId);
            return;
        }


        // 此时有相关的 bookId
        library.removeBookByIndex(index);


    }

    //统计每本书的借阅次数
    public void borrowCount() {
        Book[] books = library.getAllBooks();
        System.out.println("-----------------------");
        for (int i = 0; i < library.getBooksCount(); i++) {
            Book book = books[i];
            System.out.println("索引：" + book.getBookId() + "书名：" + book.getTitle() + " 借阅次数：" + book.getBorrowCount());

        }
        System.out.println("-----------------------");
    }

    //查询最受欢迎的前n本书
    public void generateBook() {
        Integer booksCount = library.getBooksCount();

        System.out.println("请输入要查找的前 n 本书, 要求不超过 " + booksCount);

        library.generateBook(in.nextInt());

    }

    //查看库存状态
    public void checkInventoryStatus() {
        library.checkInventoryStatus();
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
