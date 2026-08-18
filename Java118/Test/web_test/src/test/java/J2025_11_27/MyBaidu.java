package J2025_11_27;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.DriverUtil;

import java.util.List;

public class MyBaidu {
    public void search(String info) throws InterruptedException {
        WebDriver driver = DriverUtil.start("https://www.baidu.com/");
        WebElement ele = driver.findElement(By.cssSelector("#chat-textarea"));

        Thread.sleep(1000);
        ele.sendKeys("this is a test...");
        Thread.sleep(1000);
        ele.clear();
        Thread.sleep(1000);
        // 输入信息
        ele.sendKeys(info);

        Thread.sleep(3000);
        // 查询
        driver.findElement(By.xpath("//*[@id=\"chat-submit-button\"]")).click();
        Thread.sleep(3000);
        driver.quit();
    }

    public void getMsg() {
        WebDriver driver = DriverUtil.start("https://www.baidu.com/");
        DriverUtil.showTitleAndUrl(driver, "========== 获取地址与标题 =============");

        List<WebElement> hotInfo = driver.findElements(By.xpath("//*[@id=\"hotsearch-content-wrapper\"]/li/a/span[2]"));
        System.out.println("热点信息：");
        for(WebElement info : hotInfo) {
            System.out.println(info.getText());
        }

        WebElement input = driver.findElement(By.cssSelector("#chat-textarea"));
        System.out.println("输入框信息：" + input.getAttribute("placeholder"));

        driver.quit();
    }

    public void testHandle() {
        WebDriver driver = DriverUtil.start("https://www.baidu.com/");
        WebElement imageEle = driver.findElement(By.cssSelector("#s-top-left > a:nth-child(6)"));
        DriverUtil.showTitleAndUrl(driver, "=========== 点击 imageEle 之前 ==========");
        System.out.println("当前句柄：" + driver.getWindowHandle());
        System.out.println("句柄组：" + driver.getWindowHandles());

        imageEle.click();

        // 句柄不变，所以 title 与 url 不会变
        DriverUtil.showTitleAndUrl(driver, "=========== 点击 imageEle 之后（更改句柄之前） ==========");

        System.out.println("当前句柄：" + driver.getWindowHandle());
        System.out.println("句柄组：" + driver.getWindowHandles());

        // 更改句柄
        for(String handle: driver.getWindowHandles()) {
            if (!handle.equals(driver.getWindowHandle())) {
                driver.switchTo().window(handle);
            }
        }

        DriverUtil.showTitleAndUrl(driver, "=========== 更改句柄之后 ==========");
        driver.close();
//        driver.quit();
    }

    public void testSize() throws InterruptedException {
        WebDriver driver = DriverUtil.start("https://www.baidu.com/");

        Thread.sleep(2000);
        driver.manage().window().maximize();
        Thread.sleep(2000);
        driver.manage().window().minimize();
        Thread.sleep(2000);
        driver.manage().window().fullscreen();
        Thread.sleep(2000);
        driver.manage().window().setSize(new Dimension(1024, 768));
        Thread.sleep(2000);

        driver.quit();
    }
}
