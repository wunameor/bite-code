package com.wunamor.bookdemo.mapper;

import com.wunamor.bookdemo.model.BookInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;


    @Test
    void addBook() {
        List<BookInfo> bookList = getBookList(15);
        bookList.forEach(bookInfo -> {
            bookMapper.addBook(bookInfo);
        });
    }

    private List<BookInfo> getBookList(int len) {
        List<BookInfo> list = new ArrayList<>(len);
        for (int i = 1; i <= len; i++) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookName("书名-" + new Random().nextInt(100));
            bookInfo.setAuthor("作者-" + new Random().nextInt(100));
            bookInfo.setStatus(new Random().nextInt(10) % 10 != 0 ? 1 : 2);
            bookInfo.setCount(new Random().nextInt(100));
            bookInfo.setPrice(BigDecimal.valueOf(new Random().nextInt(100) / 10.0));
            bookInfo.setPublish("出版社-" + new Random().nextInt(100));
            list.add(bookInfo);
        }
        return list;
    }
}
