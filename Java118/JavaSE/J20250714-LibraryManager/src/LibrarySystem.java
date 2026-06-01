// 项目启动入口

import user.ProxyUser;
import user.User;
import user.factory.AdminUserFactory;
import user.factory.NormalUserFactory;
import user.factory.UserFactory;

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
    }
}
