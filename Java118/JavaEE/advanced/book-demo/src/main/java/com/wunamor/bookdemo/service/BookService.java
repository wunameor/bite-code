package com.wunamor.bookdemo.service;

import com.wunamor.bookdemo.model.BookInfo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BookService {
    public List<BookInfo> getList() {
        List<BookInfo> books = getDao();
        for (BookInfo book : books) {
            book.setStatusCN(book.getStatus() == 0 ? "不可借阅" : "可借阅");
        }

        return books;
    }

    // 这个是 Mock 元素，非真真实的
    private List<BookInfo> getDao() {
        int len = 15;
        List<BookInfo> list = new ArrayList<>(len);
        for (int i = 1; i <= len; i++) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setId(i);
            bookInfo.setBookName("书名-" + new Random().nextInt(100));
            bookInfo.setAuthor("作者-" + new Random().nextInt(100));
            bookInfo.setStatus(i % 5 == 0 ? 0 : 1);
            bookInfo.setCount(new Random().nextInt(100));
            bookInfo.setPrice(BigDecimal.valueOf(new Random().nextInt(100) / 10.0));
            bookInfo.setPublish("出版社-" + new Random().nextInt(100));
            list.add(bookInfo);
        }
        return list;
    }
}
