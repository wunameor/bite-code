import J2025_11_22.MyBlog;
import J2025_11_27.MyBaidu;

public class Test {

    public static void main(String[] args) throws InterruptedException {
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
