package J2025_11_22;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.DriverUtil;

import java.util.List;

public class MyBlog {

    public void test1() {
        WebDriver driver =  DriverUtil.start("https://blog.774822.xyz/01-%E8%AE%A1%E7%AE%97%E6%9C%BA%E4%B8%8EIT%E6%8A%80%E6%9C%AF/");
        System.out.println(driver.findElement(By.xpath("//*[@id=\"dir-title\"]")).getText());

        DriverUtil.close(driver);
    }

    public void test2() {
        WebDriver driver = DriverUtil.start("https://blog.774822.xyz/01-%E8%AE%A1%E7%AE%97%E6%9C%BA%E4%B8%8EIT%E6%8A%80%E6%9C%AF/");

        List<WebElement> elements = driver.findElements(By.xpath("//*[@id=\"VPContent\"]/div/div/div[2]/div/main/div/div/ul/li"));
        for(WebElement element : elements) {
            System.out.println(element.getText());
        }
        DriverUtil.close(driver);
    }


}
