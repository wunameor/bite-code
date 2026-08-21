package J2025_11_29;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverUtil;

import java.time.Duration;

public class Wait {
    private WebDriver driver;

    {
        driver = DriverUtil.start("https://github.com/Nagi-ovo/voyager");
    }

    // 这个通常用于测试初期
    public void forceWait() throws InterruptedException {
        Thread.sleep(1000);
        findEle();
        close();
    }

    // 隐式等待是全局的
    public void implicitWait() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        findEle();
        close();
    }

    // 显示等待 灵活性高，但是编写复杂
    public void showWait() {
        new WebDriverWait(driver, Duration.ofSeconds(1)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#repos-split-pane-content > div > div > div > div.prc-PageLayout-PaneWrapper-pHPop.pr-2 > div.prc-PageLayout-Pane-AyzHK > div > div:nth-child(2) > h2 > span > span.ml-1.prc-CounterLabel-CounterLabel-X-kRU")
        ));
        close();
    }


    private void findEle() {
        // 如果没有等待，应该会报错
        WebElement element = driver.findElement(By.cssSelector("#repos-split-pane-content > div > div > div > div.prc-PageLayout-PaneWrapper-pHPop.pr-2 > div.prc-PageLayout-Pane-AyzHK > div > div:nth-child(2) > h2 > span > span.ml-1.prc-CounterLabel-CounterLabel-X-kRU"));
        System.out.println("发布版本个数: " + element.getText());
    }

    private void close() {
        driver.quit();
    }
}
