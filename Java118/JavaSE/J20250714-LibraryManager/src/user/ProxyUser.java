package user;

import book.Book;
import book.Library;
import enums.AdminMenuChoice;
import enums.NormalMenuChoice;
import exception.UserPermissionException;

import java.time.LocalDateTime;
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
        ((AdminUser)proxyUser).addBook();
    }

    //移除图书
    public void removeBook() {
        checkAdminUserPermissions();
        ((AdminUser)proxyUser).removeBook();

    }

    //更新书籍操作
    public void updateBook() {
        checkAdminUserPermissions();
        System.out.println("更新书籍操作...");

    }

    //查看图书的借阅次数
    public void borrowCount() {
        checkAdminUserPermissions();
        System.out.println("查看图书的借阅次数...");
    }


    //查看最受欢迎的前K本书
    public void generateBook() {
        checkAdminUserPermissions();
        System.out.println("查看最受欢迎的前K本书...");
    }

    //查看库存状态
    public void checkInventoryStatus() {
        checkAdminUserPermissions();
        System.out.println("查看库存状态...");

    }

    //按照类别 统计图书
    public void categorizeBooksByCategory() {
        checkAdminUserPermissions();
        System.out.println("按照类别 统计图书...");
    }

    //按照作者 统计图书
    public void categorizeBooksByAuthor() {
        checkAdminUserPermissions();
        System.out.println("按照作者 统计图书...");
    }

    //移除上架超过1年的书籍
    public void checkAndRemoveOldBooks() {
        checkAdminUserPermissions();
        System.out.println("移除上架超过1年的书籍...");
    }

    //--------------------------------普通相关⽅法------------------------------//
    //借阅图书
    public void borrowBook() {
        System.out.println("借阅图书...");
    }

    //归还图书
    public void returnBook() {
        System.out.println("归还图书...");
    }

    //查看个⼈借阅情况
    public void viewBorrowHistory() {
        System.out.println("查看个⼈借阅情况...");
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
                case SEARCH_BOOKS:
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
                case SEARCH_MOST_WELCOME_BOOKS:
                    generateBook();
                    break;
                case CHECK_INVENTORY_STATUS:
                    checkInventoryStatus();
                    break;
                case SEARCH_BOOKS_BY_CATEGORY:
                    categorizeBooksByCategory();
                    break;
                case SEARCH_BOOKS_BY_AUTHOR:
                    categorizeBooksByAuthor();
                    break;
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
                    break;
                case PRINT_ALL_BOOKS:
                    break;
                case EXIT_SYSTEM:
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

    private void checkAdminUserPermissions() {
        if (!(proxyUser instanceof AdminUser)) {
            throw new UserPermissionException("非管理员权限掉用管理员功能");
        }
    }

    private void checkNormalUserPermissions() {
        if (!(proxyUser instanceof AdminUser)) {
            throw new UserPermissionException("非普通权限掉用普通功能");
        }
    }

    private String getInputValue() {
        if (in.hasNextLine()) {
            in.nextLine();
        }
        return in.nextLine();
    }
}
