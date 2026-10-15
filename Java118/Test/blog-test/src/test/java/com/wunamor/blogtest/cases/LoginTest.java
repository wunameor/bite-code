package com.wunamor.blogtest.cases;

import com.wunamor.blogtest.common.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;

public class LoginTest extends BaseTest {

    private final static String url = "https://live-blog.774821.xyz/blog_login.html";

    public LoginTest() {
        super(url);
    }

    /**
     * 清理输入框的内容
     */
    private void clearLoginInput() {
        driver.findElement(By.xpath("//*[@id=\"username\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"password\"]")).clear();
    }


    /**
     * 查看页面是否完整
     * @throws IOException
     */
    public void checkLoginPage() throws IOException {
        clearLoginInput();
        screenshot(Thread.currentThread().getStackTrace()[1].getMethodName()); // 这个是可以使用一个方法来获取到方法名称的
        // 验证导航栏
        driver.findElement(By.xpath("/html/body/div[1]/span"));
        driver.findElement(By.xpath("/html/body/div[1]/a[1]"));

        // 登录相关输入框
        driver.findElement(By.xpath("//*[@id=\"username\"]"));
        driver.findElement(By.xpath("//*[@id=\"password\"]"));
        driver.findElement(By.xpath("//*[@id=\"submit\"]"));
    }

    /**
     * 用户错误登录测试
     * @throws IOException
     */
    public void loginErrorTest() throws IOException {
        clearLoginInput();
        String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
        screenshot(funcName);
        driver.findElement(By.xpath("//*[@id=\"username\"]")).sendKeys("zhangsan");
        driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys("1234");
        driver.findElement(By.xpath("//*[@id=\"submit\"]")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept(); // 在点击确定之前不能执行任何操作，因为弹窗会阻塞整个浏览器
    }

    /**
     * 用户正确登录测试
     * @throws IOException
     */
    public void loginTrueTest() throws IOException {
        clearLoginInput();
        String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
        screenshot(funcName);
        driver.findElement(By.xpath("//*[@id=\"username\"]")).sendKeys("zhangsan");
        driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys("123456");
        driver.findElement(By.xpath("//*[@id=\"submit\"]")).click();

        screenshot(funcName);
        driver.findElement(By.xpath("/html/body/div[1]/a[3]"));// 查看是否登录成功
    }

}
