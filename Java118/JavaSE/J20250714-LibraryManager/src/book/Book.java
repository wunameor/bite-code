package book;


import java.time.LocalDateTime;

public class Book implements Comparable<Book> {
    private int bookId;    //书id
    private String title;    //书名
    private String author;    //作者
    private String category;    //类别
    private int publishYear;    //出版年份
    private boolean isBorrowed;    //借阅状态
    private int borrowCount;    //借阅次数
    private LocalDateTime shelfDate; // 上架日期



    public Book(String title, String author, String category,
                int publishYear, LocalDateTime shelfDate) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.publishYear = publishYear;
        this.shelfDate = shelfDate;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void setBorrowCount(int borrowCount) {
        this.borrowCount = borrowCount;
    }

    public LocalDateTime getShelfDate() {
        return shelfDate;
    }

    public void setShelfDate(LocalDateTime shelfDate) {
        this.shelfDate = shelfDate;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", publishYear=" + publishYear +
                ", isBorrowed=" + isBorrowed +
                ", borrowCount=" + borrowCount +
                ", shelfDate=" + shelfDate +
                '}';
    }

    @Override
    public int compareTo(Book o) {
        return 0;
    }
}
