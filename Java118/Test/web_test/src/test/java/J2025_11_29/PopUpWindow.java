package J2025_11_29;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverUtil;
import utils.FileUtil;

import java.time.Duration;

// 弹窗
public class PopUpWindow {
    private WebDriver driver;

    {
        driver = DriverUtil.start(FileUtil.getLocalPageUrl(""));
    }

    public void alertWaitTest() {
        driver.get(FileUtil.getLocalPageUrl("files/alert.html"));

        // 点击
        driver.findElement(By.cssSelector("#tooltip")).click();

        // 等待
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        alert.accept();

        driver.quit();
    }

    public void alert() throws InterruptedException {

        driver.get(FileUtil.getLocalPageUrl("files/alert.html"));
//        driver.get("file:///D:/learn/bite/bite-code/Java118/Test/web_test/files/alert.html");

        // 点击，触发弹窗
        driver.findElement(By.xpath("//*[@id=\"tooltip\"]")).click();
        Thread.sleep(2000);

        // 切换到弹窗（可能是因为弹窗在一个句柄中同一时刻只能有一个，所以它就没有参数区别）
        Alert alert = driver.switchTo().alert();
        String text = alert.getText();
        System.out.println("text: " + text);

        // 这两个都可以
        alert.accept();
//        alert.dismiss();

        Thread.sleep(2000);
        driver.quit();
    }

    public void confirm() throws InterruptedException {
        driver.get(FileUtil.getLocalPageUrl("files/confirm.html"));

        handleOfConfirm(true);
        driver.navigate().refresh();
        handleOfConfirm(false);

        driver.quit();
    }

    private void handleOfConfirm(boolean isAccept) throws InterruptedException {
        System.out.println("===================");
        // 点击，触发弹窗
        driver.findElement(By.xpath("/html/body/input")).click();
        Thread.sleep(2000);

        // 切换到弹窗（可能是因为弹窗在一个句柄中同一时刻只能有一个，所以它就没有参数区别）
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        System.out.println("alertText: " + alertText);

        // 通过传入的变量判定
        String type;
        if (isAccept) {
            alert.accept();
            type = "accept";
        } else {
            alert.dismiss();
            type = "dismiss";
        }

        String textAfterConfirm = driver.findElement(By.xpath("/html/body")).getText();
        System.out.println("textAfterConfirm(" + type + "): " + textAfterConfirm);

        Thread.sleep(2000);
    }

    public void prompt() throws InterruptedException {
        driver.get(FileUtil.getLocalPageUrl("files/Prompt.html"));

        // 点击
        driver.findElement(By.xpath("/html/body/input")).click();
        Thread.sleep(2000);

        // 输入并关闭 alert
        Alert alert = driver.switchTo().alert();
        alert.sendKeys("this is a prompt test");
        Thread.sleep(2000);
        alert.accept();

        WebElement resultEle = driver.findElement(By.xpath("/html/body"));
        System.out.println("result: " + resultEle.getText());

        driver.quit();
    }
}
