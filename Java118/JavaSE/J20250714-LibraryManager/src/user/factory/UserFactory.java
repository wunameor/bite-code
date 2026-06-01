package user.factory;

import user.User;

public interface UserFactory {
    User createUser(Integer userId, String name);
}
