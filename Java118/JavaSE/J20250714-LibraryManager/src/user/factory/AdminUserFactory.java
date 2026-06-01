package user.factory;

import user.AdminUser;
import user.User;

public class AdminUserFactory implements UserFactory{
    @Override
    public User createUser(Integer userId, String name) {
        return new AdminUser(userId, name);
    }
}
