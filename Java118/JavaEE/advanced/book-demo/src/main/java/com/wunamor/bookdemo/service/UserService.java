package com.wunamor.bookdemo.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    public Boolean login(String name, String password) {
        if ("admin".equals(name) && "123456".equals(password)) {
            return true;
        }
        return false;
    }
}
