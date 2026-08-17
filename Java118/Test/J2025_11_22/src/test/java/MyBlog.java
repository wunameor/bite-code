import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

public class MyBlog {
    private WebDriver start(String name) {
        // 打开驱动
        WebDriverManager.chromedriver().setup();

        // 添加配置
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get(name);
        return driver;
    }

    public void test1() {
        WebDriver driver = start("https://blog.774822.xyz/01-%E8%AE%A1%E7%AE%97%E6%9C%BA%E4%B8%8EIT%E6%8A%80%E6%9C%AF/");
        System.out.println(driver.findElement(By.xpath("//*[@id=\"dir-title\"]")).getText());

        close(driver);
    }

    public void test2() {
        WebDriver driver = start("https://blog.774822.xyz/01-%E8%AE%A1%E7%AE%97%E6%9C%BA%E4%B8%8EIT%E6%8A%80%E6%9C%AF/");

        List<WebElement> elements = driver.findElements(By.xpath("//*[@id=\"VPContent\"]/div/div/div[2]/div/main/div/div/ul/li"));
        for(WebElement element : elements) {
            System.out.println(element.getText());
        }
        close(driver);
    }

    private void close(WebDriver driver) {
        driver.quit();
    }
}
