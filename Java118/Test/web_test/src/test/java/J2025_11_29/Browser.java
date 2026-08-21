package J2025_11_29;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.DriverUtil;

public class Browser {
    private WebDriver driver;

    public void configTest() {
        // 打开驱动
        WebDriverManager.chromedriver().setup();

        // 添加配置
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
//        options.setPageLoadStrategy(PageLoadStrategy.NONE);
//        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
//        options.addArguments("-headless");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("https://blog.774822.xyz/");
        System.out.println("title: " + driver.getTitle());

        driver.quit();
    }
}
