package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverUtil {
    public static WebDriver start(String name) {
        // 打开驱动
        WebDriverManager.chromedriver().setup();

        // 添加配置
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get(name);
        return driver;
    }

    public static void showTitleAndUrl(WebDriver driver, String msg) {
        if (!msg.isEmpty()) System.out.println(msg);
        System.out.println("标题：" + driver.getTitle());
        System.out.println("路径：" + driver.getCurrentUrl());
    }

    public static void close(WebDriver driver) {
        driver.quit();
    }
}
