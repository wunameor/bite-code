package book;

import constants.Constants;
import utils.AnalyzingBook;
import utils.AnalyzingBorrowedBook;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Library {
    private static Library library;

    private AnalyzingBook analyzingBook = new AnalyzingBook();
    private AnalyzingBorrowedBook analyzingBorrowedBook = new AnalyzingBorrowedBook();
    private Book[] allBooks;
    private Integer booksCount;
    private Integer nextUUID = 0;

    private Library() {
        loadBooks();
    }


    public static Library getLibrary() {
        if (library == null) {
            library = new Library();
        }
        return library;
    }

    public AnalyzingBook getAnalyzingBook() {
        return analyzingBook;
    }

    public void setAnalyzingBook(AnalyzingBook analyzingBook) {
        this.analyzingBook = analyzingBook;
    }

    public Book[] getAllBooks() {
        return allBooks;
    }

    public void setAllBooks(Book[] allBooks) {
        this.allBooks = allBooks;
    }

    public Integer getBooksCount() {
        return booksCount;
    }


    private void loadBooks() {
        Book[] allBooks = analyzingBook.loadObject(Constants.BOOKS_FILE_NAME);
        Book[] books = new Book[Constants.BOOKS_DEFAULT_SIZE];

        if (allBooks.length > books.length) {
            books = allBooks;
        } else {
            for (int i = 0; i < allBooks.length; i++) {
                books[i] = allBooks[i];
            }
        }

        // 获取最大的Id
        for (int i = 0; i < books.length && books[i] != null; i++) {
            this.nextUUID = Math.max(this.nextUUID, books[i].getBookId());
        }
        this.nextUUID++;
        this.allBooks = books;
        this.booksCount = allBooks.length;
    }

    private void storeBooks() {
        if (this.allBooks == null) {
            return;
        }
        analyzingBook.storeObject(this.allBooks, Constants.BOOKS_FILE_NAME);
    }

    public void exit() {
        // storeBooks();
        System.out.println("退出系统...");
        System.exit(0);
    }

    public void addBook(Book book) {
        if (booksCount == allBooks.length) {
            // 增加长度
            this.allBooks = Arrays.copyOf(this.allBooks, allBooks.length * 2);
        }
        book.setBookId(nextUUID++);
        this.allBooks[booksCount++] = book;
        storeBooks();
    }

    public Book getBookById(int bookId) {
        Integer index = getBookIndexById(bookId);
        return index == null ? null : allBooks[index];
    }

    public Integer getBookIndexById(int bookId) {
        for (int i = 0; i < booksCount; i++) {
            if (allBooks[i].getBookId().equals(bookId)) {
                return i;
            }
        }
        return null;
    }

    public void updateBookById(Book newBook) {
        Integer index = getBookIndexById(newBook.getBookId());
        if (newBook.getAuthor() != null && !newBook.getAuthor().isEmpty()) {
            allBooks[index].setAuthor(newBook.getAuthor());
        }
        if (newBook.getCategory() != null && !newBook.getCategory().isEmpty()) {
            allBooks[index].setCategory(newBook.getCategory());
        }
        if (newBook.getTitle() != null && !newBook.getTitle().isEmpty()) {
            allBooks[index].setTitle(newBook.getTitle());
        }

        storeBooks();
    }

    public void removeBookById(Integer bookId) {
        removeBookByIndex(getBookIndexById(bookId));
    }

    public void removeBookByIndex(Integer index) {
        if (index == null || index < 0 || index >= booksCount) {
            System.out.println("下标异常： index = " + index + " booksCount = " + booksCount);
            return;
        }

        if (allBooks[index].isBorrowed()) {
            System.out.println("借阅的书籍不能删除： book: " + allBooks[index]);
            return;
        }
        Book tmp = allBooks[index];
        for (int i = index; i < booksCount - 1; i++) {
            allBooks[i] = allBooks[i + 1];
        }
        allBooks[booksCount - 1] = null;

        booksCount--;
        storeBooks();
        System.out.println("删除成功：book = " + tmp);
    }

    public void generateBook(int n) {
        if (n < 0 || n > booksCount) {
            return;
        }

        Book[] tmp = Arrays.copyOf(allBooks, booksCount);

        // Lambda 表达式
        Arrays.sort(tmp, (o1, o2) -> o2.getBorrowCount() - o1.getBorrowCount());

        System.out.println("-----------------------");
        for (int i = 0; i < n; i++) {
            Book book = tmp[i];
            System.out.println("书名：" + book.getTitle() + " 作者： " + book.getAuthor() + " 借阅次数：" + book.getBorrowCount());
        }
        System.out.println("-----------------------");
    }

    public void checkInventoryStatus() {
        System.out.println("-----------------------");
        for (int i = 0; i < booksCount; i++) {
            Book book = allBooks[i];
            System.out.println("图书索引: " + book.getBookId() + " 图书名称: " + book.getTitle() +
                    " 图书状态：" + (book.isBorrowed() ? "借出" : "未借出"));
        }
        System.out.println("-----------------------");
    }

    public List<Integer> getOldBooksId() {
        List<Integer> list = new ArrayList<>();
        // 获取当前时间戳
        long currentTimestamp = System.currentTimeMillis();


        LocalDate currentDate = Instant.ofEpochMilli(currentTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        for (int i = 0; i < getBooksCount(); i++) {
            Book book = allBooks[i];
            //获取当前书籍的上架时间
            LocalDate specifiedDate = book.getShelfDate();
            // 计算两个⽇期之间的差值（以年为单位）
            long yearsBetween = ChronoUnit.YEARS.between(specifiedDate, currentDate);
            if (yearsBetween >= 1) {
                list.add(book.getBookId());
            }
        }

        return list;
    }

    public Book getBookByIndex(Integer index) {
        if (index < 0 || index >= booksCount) {
            return null;
        }
        return allBooks[index];
    }

    public void printNoBorrowedBooks() {
        loadBooks();

        for (int i = 0; i < booksCount; i++) {
            Book book = allBooks[i];
            if (!book.isBorrowed()) {
                System.out.println(book);
            }
        }
    }

    public boolean borrowBook(Integer bookId) {
        loadBooks();

        Book book = library.getBookById(bookId);
        if (book == null || book.isBorrowed()) {
            System.out.println("未找到相关书籍或者 book 已经借出，不能再借阅。 book: " + book);
            return false;
        }

        book.setBorrowed(true);
        book.setBorrowCount(book.getBorrowCount() + 1);
        storeBooks();

        return true;
    }

    public void printBooksById(List<Integer> bookIdByUserId) {
        loadBooks();
        for (Integer bookId : bookIdByUserId) {
            System.out.println(getBookById(bookId));
        }
    }

    public boolean returnBook(int bookId) {
        loadBooks();

        Book book = getBookById(bookId);
        if (book == null || !book.isBorrowed()) {
            System.out.println("未找到相关书籍或者 book 已经借出，不能再借阅。 book: " + book);
            return false;
        }

        book.setBorrowed(false);
        storeBooks();

        return true;
    }
}
