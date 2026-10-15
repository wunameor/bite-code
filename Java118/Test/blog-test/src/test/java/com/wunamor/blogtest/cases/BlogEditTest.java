package com.wunamor.blogtest.cases;

import com.wunamor.blogtest.common.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class BlogEditTest extends BaseTest {
    private final static String url = "https://live-blog.774821.xyz/blog_edit.html";

    public BlogEditTest() {
        super(url);
    }

    public void submitBlogTest() throws InterruptedException {
        String dateTime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(System.currentTimeMillis());

        String title = "自动化测试-" + dateTime;
        String content = "##这是一个自动化测试内容" + dateTime;

        // 正文 使用 driver 上层的 API 是无法直接输入元素的，需要使用 Action 来插入元素
        Thread.sleep(5000);
        WebElement contentEle = driver.findElement(By.xpath("//*[@id=\"editor\"]/div[1]/div[6]/div[1]/div/div/div/div[5]/div/pre/span/span"));
        new Actions(driver)
                .keyDown(contentEle, Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .sendKeys(content)
                .perform();

        Thread.sleep(2000);

        // 标题
        driver.findElement(By.xpath("//*[@id=\"title\"]")).sendKeys(title);

        driver.findElement(By.xpath("//*[@id=\"submit\"]")).click();

        Thread.sleep(1000);
//        System.out.println("driver.getCurrentUrl() = " + driver.getCurrentUrl());
        // /html/body/div[2]/div[2]/div[5]
        List<WebElement> blogs = driver.findElements(By.xpath("/html/body/div[2]/div[2]/div"));
        // /html/body/div[2]/div[2]/div[7]/div[1]
        System.out.println("size: " + blogs.size());

        String listTitle = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[" + blogs.size() + "]/div[1]")).getText();
        // /html/body/div[2]/div[2]/div[5]/div[3]
        String listContent = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[" + blogs.size() + "]/div[3]")).getText();

        System.out.println("title: " + title);
        System.out.println("listTitle: " + listTitle);
        assert title.equals(listTitle);
        assert content.equals(listContent);
    }
}
