package com.wunamor.blogtest;

import com.wunamor.blogtest.cases.BlogDetailTest;
import com.wunamor.blogtest.cases.BlogEditTest;
import com.wunamor.blogtest.cases.BlogListTest;
import com.wunamor.blogtest.cases.LoginTest;
import com.wunamor.blogtest.common.BaseTest;

import java.io.IOException;

public class Main {
    private static void loginTest() throws IOException {
        LoginTest loginTest = new LoginTest();
        loginTest.checkLoginPage();
        loginTest.loginErrorTest();
        loginTest.loginTrueTest();
    }

    private static void blogListTest() throws IOException {
        BlogListTest blogListTest = new BlogListTest();
        blogListTest.checkBlogList();
    }

    private static void blogEditTest() throws IOException, InterruptedException {
        BlogEditTest blogEditTest = new BlogEditTest();
        blogEditTest.submitBlogTest();
    }

    private static void blogDetailTest() {
        BlogDetailTest blogDetailTest = new BlogDetailTest();
        blogDetailTest.checkBlogDetail();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        loginTest();
        blogListTest();
        blogDetailTest();
        blogEditTest();
        BaseTest.quit();
    }
}
