package com.wunamor.blogtest.cases;

import com.wunamor.blogtest.common.BaseTest;
import org.openqa.selenium.By;

public class BlogListTest extends BaseTest {
    private final static String url = "https://live-blog.774821.xyz/blog_list.html";
    public BlogListTest() {
        super(url);
    }

    public void checkBlogList() {
        String title = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[1]")).getText();
        String date = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]")).getText();
        String content = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[3]")).getText();
        String buttenText = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/a")).getText();

        assert !title.isEmpty();
        assert !date.isEmpty();
        assert !content.isEmpty();
        assert buttenText.equals("查看全文>>");

        String blogHref = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/a")).getAttribute("href");
        blogDetailUrl = blogHref;
    }

}
