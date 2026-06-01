package user;

import java.util.Scanner;

public abstract class User {
    protected Scanner scanner = new Scanner(System.in);

    protected Integer userId;
    protected String name;
    protected String role;

    public User(Integer userId, String name, String role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    /**
     * 面板展示
     */
    public abstract int display();

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
