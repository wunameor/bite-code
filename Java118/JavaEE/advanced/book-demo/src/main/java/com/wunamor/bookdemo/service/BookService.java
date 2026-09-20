package com.wunamor.bookdemo.service;

import com.wunamor.bookdemo.enums.BookStatusEnum;
import com.wunamor.bookdemo.mapper.BookMapper;
import com.wunamor.bookdemo.model.BookInfo;
import com.wunamor.bookdemo.model.PageRequest;
import com.wunamor.bookdemo.model.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;


    public PageResponse<BookInfo> getListByPage(PageRequest pageRequest) {
        int count = bookMapper.getTotalCount();
        if (count == 0) {
            return new PageResponse<>(0, pageRequest.getCurrentPage());
        }

        // 获取图书列表
        List<BookInfo> books = bookMapper.getBookList(pageRequest);

        // 转换状态
        for (BookInfo book : books) {
            book.setStatusCN(BookStatusEnum.getNameByCode(book.getStatus()));
        }

        return new PageResponse<>(count, pageRequest.getCurrentPage(), books);
    }

    public List<BookInfo> getList() {
        List<BookInfo> books = getDao();
        for (BookInfo book : books) {
            book.setStatusCN(book.getStatus() == 0 ? "不可借阅" : "可借阅");
        }

        return books;
    }

    // 这个是 Mock 元素，非真真实的 而且应该放在 dao 层
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

    public void addBook(BookInfo bookInfo) {
        Integer result = bookMapper.addBook(bookInfo);
    }

    public Boolean deleteBookById(Integer bookId) {
        return false;
    }

    public String batchDeleteBookByIds(List<Integer> bookIds) {
        Integer count = bookMapper.batchDeleteBookByIds(bookIds);

        if (count != bookIds.size()) {
            log.warn("删除出现异常，删除个数不匹配：count = {}, bookIds.size = {}", count, bookIds.size());
            return "删除出现异常，删除个数不匹配";
        }

        return "";
    }

    public BookInfo getBookById(Integer bookId) {
        return bookMapper.getBookById(bookId);
    }

    public Boolean updateBook(BookInfo bookInfo) {

        try {
            return bookMapper.updateBook(bookInfo);
        } catch (Exception e) {
            log.error("图书更新出现错误：e {}", e);
            return false;
        }
    }
}
