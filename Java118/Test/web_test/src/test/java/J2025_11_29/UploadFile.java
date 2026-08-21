package J2025_11_29;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverUtil;
import utils.FileUtil;

import java.time.Duration;

public class UploadFile {
    private WebDriver driver;

    {
        driver = DriverUtil.start(FileUtil.getLocalPageUrl("files/upload.html"));
    }

    public void upload() throws InterruptedException {
        WebElement fileInput = driver.findElement(By.cssSelector("body > div > div > input[type=file]"));

        fileInput.sendKeys(FileUtil.getAbsolutePath("files/alert.html"));
        Thread.sleep(2000);

        // 这里是为 ""
        System.out.println("file text: " + fileInput.getText());
        driver.quit();
    }
}
