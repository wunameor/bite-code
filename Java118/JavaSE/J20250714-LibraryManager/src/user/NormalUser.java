package user;

import book.Library;
import book.PairOfUidAndBookId;
import constants.Constants;
import utils.AnalyzingBorrowedBook;

import java.util.*;

public class NormalUser extends User {

    private Scanner in = new Scanner(System.in);
    private Library library = Library.getLibrary();
    private AnalyzingBorrowedBook analyzingBorrowedBook = new AnalyzingBorrowedBook();
    private List<PairOfUidAndBookId> pairOfUidAndBookIdList;


    public NormalUser(Integer userId, String name, String role) {
        super(userId, name, role);
        loadBorrowedBook();
    }


    public NormalUser(Integer userId, String name) {
        super(userId, name, "普通用户"); // 一般放在 constants 里面，而不是硬编码
        loadBorrowedBook();
    }


    private void loadBorrowedBook() {
        PairOfUidAndBookId[] array = analyzingBorrowedBook.loadObject(Constants.BOOKS_USERS_FILE_NAME);

        this.pairOfUidAndBookIdList = new ArrayList<>(Arrays.asList(array));
    }

    private void storeBorrowedBook() {
        PairOfUidAndBookId[] array = pairOfUidAndBookIdList.toArray(new PairOfUidAndBookId[0]);
        analyzingBorrowedBook.storeObject(array, Constants.BOOKS_USERS_FILE_NAME);
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
        loadBorrowedBook();

        System.out.println("可以借阅图书如下：");
        System.out.println("-----------------------");
        library.printNoBorrowedBooks();
        System.out.println("-----------------------");

        System.out.println("请输入要借阅的图书id：");
        int bookId = in.nextInt();
        boolean flag = library.borrowBook(bookId);

        if (!flag) {
            System.out.println("借阅图书失败， bookId: " + bookId);
            return;
        }
        pairOfUidAndBookIdList.add(new PairOfUidAndBookId(bookId, userId));
        storeBorrowedBook();

        System.out.println("借阅图书成功，个人借阅图书如下");
        viewBorrowBooks();
    }

    //归还图书
    public void returnBook() {
        loadBorrowedBook();

        System.out.println("需要归还图书如下：");
        System.out.println("-----------------------");
        List<Integer> bookIdByUserId = getBookIdByUserId(userId);
        library.printBooksById(bookIdByUserId);
        System.out.println("-----------------------");

        System.out.println("请输入要归还的图书id：");
        int bookId = in.nextInt();
        if (!bookIdByUserId.contains(bookId)) {
            // 不包含 -> 改用户没有相关借阅信息
            System.out.println("用户没有借阅相关书籍： bookId = " + bookId);
            return;
        }

        boolean flag = library.returnBook(bookId);
        if (!flag) {
            System.out.println("借阅图书失败， bookId: " + bookId);
            return;
        }


        // 如果userId 不同，依然不会删除
        pairOfUidAndBookIdList.remove(new PairOfUidAndBookId(bookId, userId));
        storeBorrowedBook();
    }

    //查看个人借阅情况
    public void viewBorrowBooks() {
        loadBorrowedBook();
        List<Integer> bookIdByUserId = getBookIdByUserId(userId);
        library.printBooksById(bookIdByUserId);
    }

    private List<Integer> getBookIdByUserId(Integer userId) {
        List<Integer> ret = new LinkedList<>();

        for (PairOfUidAndBookId uidAndBookId : pairOfUidAndBookIdList) {
            if (uidAndBookId.getUserId().equals(userId)) {
                ret.add(uidAndBookId.getBookId());
            }
        }
        return ret;
    }

}
