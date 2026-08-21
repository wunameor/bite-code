import J2025_11_22.MyBlog;
import J2025_11_27.MyBaidu;
import J2025_11_29.*;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws Exception {
//        Window window = new Window();
//        window.screenshot();

//        Wait wait = new Wait();
//        wait.forceWait();
//        wait.implicitWait();
//        wait.showWait();

//        Navigate navigate = new Navigate();
//        navigate.test();

        PopUpWindow popUpWindow = new PopUpWindow();
        popUpWindow.alertWaitTest();
//        popUpWindow.alert();
//        popUpWindow.confirm();
//        popUpWindow.prompt();

//        UploadFile uploadFile = new UploadFile();
//        uploadFile.upload();

//        Browser browser = new Browser();
//        browser.configTest();
    }

    public static void main2(String[] args) throws InterruptedException {
        MyBaidu baidu = new MyBaidu();
//        baidu.search("今日热搜");
//        baidu.getMsg();
//        baidu.testHandle();
        baidu.testSize();
    }


    public static void main1(String[] args) {
        MyBlog blog = new MyBlog();
        blog.test2();
    }
}
