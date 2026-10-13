package com.wunamor.blogtest.cases;

import com.wunamor.blogtest.common.BaseTest;
import org.openqa.selenium.By;

public class BlogDetailTest extends BaseTest {
    private final static String url = blogDetailUrl;

    public BlogDetailTest() {
        super(url);
    }

    public void checkBlogDetail() {
        String title = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[1]")).getText();
        String date = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]")).getText();
        String detail = driver.findElement(By.xpath("//*[@id=\"detail\"]")).getText();

        assert !title.isEmpty();
        assert !date.isEmpty();
        assert !detail.isEmpty();
    }
}
