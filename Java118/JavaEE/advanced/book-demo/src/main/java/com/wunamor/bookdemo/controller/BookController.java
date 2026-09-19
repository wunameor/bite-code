package com.wunamor.bookdemo.controller;


import com.wunamor.bookdemo.model.BookInfo;
import com.wunamor.bookdemo.model.PageRequest;
import com.wunamor.bookdemo.model.PageResponse;
import com.wunamor.bookdemo.service.BookService;
import com.wunamor.bookdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@Slf4j
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/getList")
    public List<BookInfo> getList() {
        return bookService.getList();
    }

    @GetMapping("/getListByPage")
    public PageResponse<BookInfo> getListByPage(PageRequest pageRequest) {
        log.info("获取图书列表：pageRequest: {}", pageRequest);
        return bookService.getListByPage(pageRequest);
    }

    @PostMapping("/addBook")
    public String addBook(@RequestBody BookInfo bookInfo) {
        log.info("添加图书：bookInfo: {}", bookInfo);
        if (bookInfo == null ||
                !StringUtils.hasLength(bookInfo.getBookName()) ||
                !StringUtils.hasLength(bookInfo.getAuthor()) ||
                !StringUtils.hasLength(bookInfo.getPublish()) ||
                bookInfo.getCount() == null ||
                bookInfo.getPrice() == null ||
                bookInfo.getStatus() == null
        ) {
            log.warn("添加图书：信息不完善：bookInfo: {}", bookInfo);
            return "填入信息不完善， book: " + (bookInfo != null ? bookInfo.toString() : null);
        }

        bookService.addBook(bookInfo);
        return "";
    }
}
