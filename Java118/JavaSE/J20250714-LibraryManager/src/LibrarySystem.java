// 项目启动入口

import enums.UserProxyChoice;
import user.ProxyUser;
import user.User;
import user.factory.AdminUserFactory;
import user.factory.NormalUserFactory;
import user.factory.UserFactory;

import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) {
        UserFactory adminUserFactory = new AdminUserFactory();
        UserFactory normalUserFactory = new NormalUserFactory();


        User adminUser = adminUserFactory.createUser(1, "张老师");

        User normalUser1 = normalUserFactory.createUser(1, "张三");
        User normalUser2 = normalUserFactory.createUser(1, "李四");


        ProxyUser adminProxyUser = new ProxyUser(adminUser);
        ProxyUser normalProxyUser1 = new ProxyUser(normalUser1);
        ProxyUser normalProxyUser2 = new ProxyUser(normalUser2);

        ProxyUser proxyUser = getProxyUser();
        while (true) {
            int choice = proxyUser.display();
            proxyUser.handleOperation(choice);

        }
    }

    private static ProxyUser getProxyUser() {
        System.out.println("选择⻆⾊进⾏登录：");
        System.out.println("1.管理员\n2.普通⽤⼾(关⽻)\n3.普通⽤⼾(张⻜)\n4.退出系统");
        ProxyUser currentUser = null;
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        UserProxyChoice value = UserProxyChoice.getByValue(choice);


        switch (value) {
            case ADMIN:
                currentUser = new ProxyUser(
                        new AdminUserFactory().createUser(1, "管理员")
                );
                break;
            case NORMAL_GUANYU:
                currentUser = new ProxyUser(
                        new NormalUserFactory().createUser(1, "关羽")
                );
                break;
            case NORMAL_ZHANGFEI:
                currentUser = new ProxyUser(
                        new NormalUserFactory().createUser(2, "张飞")
                );
                break;
            case EXIT:
                System.out.println("系统已退出..");
                System.exit(0);
                break;
            default:
                break;
        }
        return currentUser;
    }
}
