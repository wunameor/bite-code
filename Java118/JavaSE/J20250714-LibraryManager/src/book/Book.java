package book;


import constants.Constants;

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
        return "[" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", publishYear=" + publishYear +
                ", isBorrowed=" + isBorrowed +
                ", borrowCount=" + borrowCount +
                ", shelfDate=" + shelfDate +
                ']';
    }

    @Override
    public int compareTo(Book o) {
        return 0;
    }


    public String toJson() {
        StringBuilder bookJson = new StringBuilder();
        bookJson.append(bookId).append(Constants.OBJECT_MEMBER_SEPARATOR)
                .append(title).append(Constants.OBJECT_MEMBER_SEPARATOR)
                .append(author).append(Constants.OBJECT_MEMBER_SEPARATOR)
                .append(category).append(Constants.OBJECT_MEMBER_SEPARATOR)
                .append(publishYear).append(Constants.OBJECT_MEMBER_SEPARATOR)
                .append(isBorrowed).append(Constants.OBJECT_MEMBER_SEPARATOR)
                .append(borrowCount).append(Constants.OBJECT_MEMBER_SEPARATOR)
                .append(shelfDate);

        return bookJson.toString();
    }

    public static Book parse(String bookLine) {
        String[] bookMember = bookLine.split(Constants.OBJECT_MEMBER_SEPARATOR);

        String bookId = bookMember[0];
        String title = bookMember[1];
        String author = bookMember[2];
        String category = bookMember[3];
        String publishYear = bookMember[4];
        String isBorrowed = bookMember[5];
        String borrowCount = bookMember[6];
        String shelfDate = bookMember[7];


        if (bookId.isEmpty() || title.isEmpty() || author.isEmpty() || category.isEmpty()
                || publishYear.isEmpty() || isBorrowed.isEmpty()
                || borrowCount.isEmpty() || shelfDate.isEmpty()) {
            return null;
        }


        LocalDateTime dateTime = LocalDateTime.parse(shelfDate);

        Book book = new Book(title, author, category,
                Integer.parseInt(publishYear), dateTime);
        book.setBookId(Integer.parseInt(bookId));
        book.setBorrowed(Boolean.parseBoolean(isBorrowed));
        book.setBorrowCount(Integer.parseInt(borrowCount));
        return book;
    }

}
