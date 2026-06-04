package book;

import constants.Constants;
import utils.AnalyzingBook;

import java.util.Arrays;

public class Library {
    private static Library library;

    private AnalyzingBook analyzingBook = new AnalyzingBook();
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

    public void setBooksCount(Integer booksCount) {
        this.booksCount = booksCount;
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
}
