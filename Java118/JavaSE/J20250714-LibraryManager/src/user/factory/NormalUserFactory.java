package user.factory;

import user.NormalUser;
import user.User;

public class NormalUserFactory implements UserFactory{
    @Override
    public User createUser(Integer userId, String name) {
        return new NormalUser(userId, name);
    }
}
