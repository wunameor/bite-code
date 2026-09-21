package com.wunamor.mybatisplusdemo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wunamor.mybatisplusdemo.model.BookInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class BookInfoMapperTest {

    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Test
    public void add() {
        BookInfo bookInfo = new BookInfo();
        bookInfo.setAuthor("张三");
        bookInfo.setBookName("母猪产后护理");
        bookInfo.setPrice(new BigDecimal("4.5"));
        bookInfo.setCount(123);
        bookInfo.setPublish("佩奇出版社");
        bookInfoMapper.insert(bookInfo);
    }

    @Test
    public void delete() {
        bookInfoMapper.deleteById(52);
    }

    @Test
    public void update() {
        UpdateWrapper<BookInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("publish", "佩奇出版社")
                .eq("id", 51);
        bookInfoMapper.update(updateWrapper);
    }

    @Test
    public void select() {
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<BookInfo>()
                .select("id", "book_name", "author") // 建议使用 Lambda 表达式
                .le("id", 5);
        List<BookInfo> bookInfos = bookInfoMapper.selectList(queryWrapper);
        System.out.println(bookInfos);
    }

    @Test
    public void lambdaSelect() {
        LambdaQueryWrapper<BookInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(BookInfo::getId, BookInfo::getBookName, BookInfo::getAuthor)
                .like(BookInfo::getBookName, "书名");

        System.out.println(bookInfoMapper.selectList(queryWrapper));
    }

    @Test
    public void customSelect() {
        LambdaQueryWrapper<BookInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(BookInfo::getId, BookInfo::getBookName, BookInfo::getAuthor)
                .in(BookInfo::getId, List.of(1,2,3));

        List<BookInfo> list = bookInfoMapper.selectByCustom(queryWrapper);
        System.out.println(list);
    }
}
