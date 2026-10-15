package com.wunamor.blogtest.common;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;

public class BaseTest {
    protected static WebDriver driver;
    protected static WebDriverWait wait;

    protected static String blogDetailUrl;

    public BaseTest(String url) {
        start(url);

    }

    /**
     * 添加 Driver 驱动
     * @param url 要访问的地址
     */
    private static void start(String url) {
        if (driver == null) {
            // 打开驱动
            WebDriverManager.chromedriver().setup();
            // 添加配置
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        }
        driver.get(url);
    }

    /**
     * 屏幕截图
     * @param funcName 方法的名称
     * @throws IOException
     */
    protected static void screenshot(String funcName) throws IOException {
        // 获取路径
        String baseUrl = "src/test/java/com/wunamor/blogtest/images/";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
        String time = new SimpleDateFormat("_HH_mm_ss").format(System.currentTimeMillis());

        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        // 生成 baseUrl/date/funcName_time
        String finalUrl = baseUrl + date + File.separator + funcName + time + ".png";
        File image = new File(finalUrl);
        // 保存，这个方法需要导入 common.io 这个包
        FileUtils.copyFile(screen, image);
    }

    public static void quit() {
        driver.quit();
    }
}
