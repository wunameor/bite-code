package com.wunamor.bookdemo.controller;


import com.wunamor.bookdemo.model.BookInfo;
import com.wunamor.bookdemo.model.PageRequest;
import com.wunamor.bookdemo.model.PageResponse;
import com.wunamor.bookdemo.service.BookService;
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

    @PostMapping("/deleteBookById")
    public String deleteBookById(Integer bookId) {
        log.info("删除图书：bookId: {}", bookId);
        if (bookId == null) {
            log.warn("删除图书-图书 Id 不能为空：bookId: {}", bookId);
            return "bookId 不能为空";
        }
        return bookService.batchDeleteBookByIds(List.of(bookId));
    }

    @PostMapping("/batchDeleteBookByIds")
    public String batchDeleteBookByIds(@RequestParam List<Integer> bookIds) {
        log.info("批量删除图书：bookId: {}", bookIds);
        if (bookIds == null || bookIds.isEmpty()) {
            log.warn("批量删除图书-图书 Ids 不能为空：bookId: {}", bookIds);
            return "bookIds 不能为空";
        }
        return bookService.batchDeleteBookByIds(bookIds);
    }

    @GetMapping("/getBookById")
    public BookInfo getBookById(Integer bookId) {
        log.info("根据 Id 获取图书信息：bookId: {}", bookId);
        if (bookId == null) {
            log.warn("根据 Id 获取图书信息-图书 Id 为空");
            return new BookInfo();
        }
        return bookService.getBookById(bookId);
    }

    @PostMapping("/updateBook")
    public Boolean updateBook(BookInfo bookInfo) {
        log.info("更新图书：bookInfo: {}", bookInfo);
        if (bookInfo == null || bookInfo.getId() == null) {
            log.warn("更新图书出现异常：bookInfo: {}", bookInfo);
            return false;
        }
        return bookService.updateBook(bookInfo);
    }
}
