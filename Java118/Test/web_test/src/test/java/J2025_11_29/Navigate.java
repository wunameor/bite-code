package J2025_11_29;

import org.openqa.selenium.WebDriver;
import utils.DriverUtil;

public class Navigate {
    private WebDriver driver;

    {
        driver = DriverUtil.start("https://blog.774822.xyz/");
    }

    public void test() throws InterruptedException {
        Thread.sleep(2000);
        // 去自动化测试的博客
        driver.get("https://blog.774822.xyz/01-%E8%AE%A1%E7%AE%97%E6%9C%BA%E4%B8%8EIT%E6%8A%80%E6%9C%AF/01-%E6%AF%94%E7%89%B9%E5%B0%B1%E4%B8%9A%E8%AF%BE/01-Java%E7%A0%94%E5%8F%91%E7%B3%BB%E7%BB%9F%E8%AF%BE118%E6%9C%9F/10-%E6%B5%8B%E8%AF%95/2025-11-20-%E8%87%AA%E5%8A%A8%E5%8C%96%E6%B5%8B%E8%AF%95#2025-11-20-%E8%87%AA%E5%8A%A8%E5%8C%96%E6%B5%8B%E8%AF%95");

        Thread.sleep(2000);
        driver.navigate().back();
        Thread.sleep(2000);
        driver.navigate().forward();
        Thread.sleep(2000);
        driver.navigate().refresh();
        Thread.sleep(2000);


        driver.quit();
    }
}
