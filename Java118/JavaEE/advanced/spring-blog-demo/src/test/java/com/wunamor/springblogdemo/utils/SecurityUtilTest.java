package com.wunamor.springblogdemo.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class SecurityUtilTest {

    @Test
    public void encrypt() {
        String password = "123456";
        String salt = UUID.randomUUID().toString().replaceAll("-", "");
        String sqlPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));

        System.out.println(salt);
        System.out.println(sqlPassword + salt);
    }

    @Test
    public void verify() {
        String password = "awfawfq2r1rqfaw";
        String sqlPassword = "e9f9cabefec19680b7dd1f8b21f9ab53891f16bd31854db5a3d68b3fab87585e";
        String salt = sqlPassword.substring(32, 64);
        System.out.println(salt);
        String inputSqlPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
        System.out.println(inputSqlPassword);
        System.out.println((inputSqlPassword + salt).equals(sqlPassword));
    }

    public static String encrypt(String password) {
        String salt = UUID.randomUUID().toString().replaceAll("-", "");
        String sqlPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));

        return sqlPassword + salt;
    }

    public static boolean verify(String password, String sqlPassword) {
        if (!StringUtils.hasText(password) || !StringUtils.hasText(sqlPassword)) {
            return false;
        }
        if (sqlPassword.length() != 64) {
            return false;
        }

        String salt = sqlPassword.substring(32, 64);

        String inputSqlPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));

        return (inputSqlPassword + salt).equals(sqlPassword);
    }

    @Test
    public void main1() {
        String encrypt = encrypt("123456");
        System.out.println(verify("123456", encrypt));
    }
}
