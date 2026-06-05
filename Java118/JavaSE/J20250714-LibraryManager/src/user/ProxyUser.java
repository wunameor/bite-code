package user;

import book.Book;
import book.Library;
import enums.AdminMenuChoice;
import enums.NormalMenuChoice;
import exception.UserPermissionException;

import java.util.List;
import java.util.Scanner;

public class ProxyUser {
    private User proxyUser;
    private Library library = Library.getLibrary();
    private Scanner in = new Scanner(System.in);

    public ProxyUser(User proxyUser) {
        this.proxyUser = proxyUser;
    }

    public User getProxyUser() {
        return proxyUser;
    }

    //调用菜单
    public int display() {
        System.out.println("调用菜单...");
        return proxyUser.display();
    }

    // 打印所有书籍
    public void printAllBooks() {
        Book[] allBooks = library.getAllBooks();
        for (Book book : allBooks) {
            if (book == null) break;
            System.out.println(book);
        }
    }

    public void exitSystem() {
        library.exit();
    }


    //添加书籍操作
    public void addBook() {
        checkAdminUserPermissions();
        ((AdminUser) proxyUser).addBook();
    }

    //移除图书
    public void removeBook() {
        checkAdminUserPermissions();
        ((AdminUser) proxyUser).removeBook();
    }

    //查看图书的借阅次数
    public void borrowCount() {
        checkAdminUserPermissions();
        System.out.println("查看图书的借阅次数...");
        ((AdminUser) proxyUser).borrowCount();
    }

    //更新书籍操作
    public void updateBook() {
        checkAdminUserPermissions();
        System.out.println("更新书籍操作...");
        ((AdminUser) proxyUser).updateBookById();
    }


    //查看最受欢迎的前K本书
    public void generateBook() {
        checkAdminUserPermissions();
        System.out.println("查看最受欢迎的前K本书...");
        ((AdminUser) proxyUser).generateBook();
    }

    //查看库存状态
    public void checkInventoryStatus() {
        checkAdminUserPermissions();
        System.out.println("查看库存状态...");
        ((AdminUser) proxyUser).checkInventoryStatus();
    }

    //按照类别 统计图书
    public void categorizeBooksByCategory() {
        checkAdminUserPermissions();
        System.out.println("按照类别 统计图书...");
        ((AdminUser) proxyUser).categorizeBooksByCategory();
    }

    //按照作者 统计图书
    public void categorizeBooksByAuthor() {
        checkAdminUserPermissions();
        System.out.println("按照作者 统计图书...");
        ((AdminUser) proxyUser).categorizeBooksByAuthor();
    }

    //移除上架超过1年的书籍
    public void checkAndRemoveOldBooks() {
        checkAdminUserPermissions();
        System.out.println("移除上架超过1年的书籍...");
        List<Integer> booksId = library.getOldBooksId();
        if (booksId.isEmpty()) {
            System.out.println("没有上架超过⼀年的图书！");
            return;
        }

        System.out.println("确认要删除超过 1 年的书籍吗？(y/N)");
        System.out.println("-----------------------");
        for (Integer id : booksId) {
            Book book = library.getBookById(id);
            System.out.println(book);
        }
        System.out.println("-----------------------");

        String flag = in.next();
        if (flag.equalsIgnoreCase("y")) {
            for (Integer id : booksId) {
                library.removeBookById(id);
            }
        }
    }

    //--------------------------------普通相关⽅法------------------------------//
    //借阅图书
    public void borrowBook() {
        checkNormalUserPermissions();
        System.out.println("借阅图书...");
        ((NormalUser) proxyUser).borrowBook();
    }

    //归还图书
    public void returnBook() {
        checkNormalUserPermissions();
        System.out.println("归还图书...");
        ((NormalUser) proxyUser).returnBook();
    }

    //查看个⼈借阅情况
    public void viewBorrowHistory() {
        checkNormalUserPermissions();
        System.out.println("查看个⼈借阅情况...");
        ((NormalUser) proxyUser).viewBorrowBooks();
    }

    /**
     * 根据用户的选择处理
     *
     * @param choice
     */
    public void handleOperation(int choice) {
        if (proxyUser instanceof AdminUser) {
            AdminMenuChoice value = AdminMenuChoice.getByValue(choice);
            switch (value) {
                case SEARCH_BOOK:
                    searchBook();
                    break;
                case PRINT_ALL_BOOKS:
                    printAllBooks();
                    break;
                case EXIT_SYSTEM:
                    exitSystem();
                    break;
                case ADD_BOOK:
                    addBook();
                    break;
                case UPDATE_BOOK:
                    updateBook();
                    break;
                case DELETE_BOOK:
                    removeBook();
                    break;
                case CALCULATE_BOOK_BORROW_COUNT:
                    borrowCount();
                    break;
                case SEARCH_MOST_WELCOME_BOOKS:
                    generateBook();
                    break;
                case CHECK_INVENTORY_STATUS:
                    checkInventoryStatus();
                    break;
//                case SEARCH_BOOKS_BY_CATEGORY:
//                    categorizeBooksByCategory();
//                    break;
//                case SEARCH_BOOKS_BY_AUTHOR:
//                    categorizeBooksByAuthor();
//                    break;
                case SEARCH_BOOKS_MORE_ONE_YEAR:
                    checkAndRemoveOldBooks();
                    break;
                default:
                    break;
            }
        } else if (proxyUser instanceof NormalUser) {
            NormalMenuChoice value = NormalMenuChoice.getByValue(choice);
            switch (value) {
                case SEARCH_BOOKS:
                    searchBook();
                    break;
                case PRINT_ALL_BOOKS:
                    printAllBooks();
                    break;
                case EXIT_SYSTEM:
                    exitSystem();
                    break;
                case BORROW_BOOK:
                    borrowBook();
                    break;
                case RETURN_BOOK:
                    returnBook();
                    break;
                case VIEW_BORROW_HISTORY:
                    viewBorrowHistory();
                    break;
            }


        }

    }

    public void searchBook() {
        System.out.println("请需要查找的输入BookId: ");
        int id = in.nextInt();
        Book book = library.getBookById(id);
        if (book == null) {
            System.out.println("没有相关图书 id " + id);
        } else {
            System.out.println(book);
        }
    }

    private void checkAdminUserPermissions() {
        if (!(proxyUser instanceof AdminUser)) {
            throw new UserPermissionException("非管理员权限掉用管理员功能");
        }
    }

    private void checkNormalUserPermissions() {
        if (!(proxyUser instanceof NormalUser)) {
            throw new UserPermissionException("非普通权限掉用普通功能");
        }
    }

}
