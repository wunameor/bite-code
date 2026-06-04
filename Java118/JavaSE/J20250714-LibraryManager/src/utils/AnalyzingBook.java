package utils;

import book.Book;
import book.Library;
import com.bit.utils.FileUtils;
import constants.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnalyzingBook {


    public Book[] loadObject(String filename) {
        String fileContent = FileUtils.readFile(filename);



        String[] booksString = fileContent.split(Constants.OBJECT_SEPARATOR);
        Book[] books = new Book[booksString.length];
        int index = 0;
        for (String bookLine : booksString) {
            Book book = Book.parse(bookLine);
            if (book != null) {
                books[index++] = book;
            }
        }

        return books;
    }

    public void storeObject(Book[] books, String fileName) {
        StringBuilder booksJson = new StringBuilder();
        for (Book book : books) {
            if (book == null) break;
            booksJson.append(book.toJson()).append(Constants.OBJECT_SEPARATOR);
        }

        FileUtils.writeFile(booksJson.toString(), fileName);
    }


    public static void main(String[] args) {
//        getBooks(args);
        storeBooks(args);
    }

    private static void getBooks(String[] args) {
        AnalyzingBook analyzingBook = new AnalyzingBook();
        Book[] books = analyzingBook.loadObject(Constants.BOOKS_FILE_NAME);
        for (Book book : books) {
            System.out.println(book.toJson());
        }
    }

    private static void storeBooks(String[] args) {
        Book[] books = new Book[4];
        books[0] = new Book("java", "gaobo", "编程", 1994, LocalDateTime.of(2023, 9, 24, 6, 12));
        books[1] = new Book("mysql", "lisi", "编程", 1999, LocalDateTime.of(2024, 2, 10, 2, 15));
        books[2] = new Book("php", "gaobo", "编程", 2020, LocalDateTime.of(2023, 9, 23, 5, 22));
        books[3] = new Book("西游记", "吴承恩", "⼩说", 2024, LocalDateTime.of(2023, 9, 23, 14, 32));
        Library library = Library.getLibrary();
        for (Book book : books) {
            library.addBook(book);
        }
//        AnalyzingBook analyzingBook = new AnalyzingBook();
//        analyzingBook.storeObject(books, Constants.BOOKS_FILE_NAME);
    }
}
