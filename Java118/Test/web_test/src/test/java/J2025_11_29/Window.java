package J2025_11_29;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.DriverUtil;

import java.io.File;
import java.io.IOException;

public class Window {
    private WebDriver driver;

    {
        driver = DriverUtil.start("https://blog.774822.xyz/");
    }

    public void screenshot() throws IOException {
        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File image = new File("screenshot-test.png");
        // 保存，这个方法需要导入 common.io 这个包
        FileUtils.copyFile(screen, image);
        driver.quit();
    }
}
