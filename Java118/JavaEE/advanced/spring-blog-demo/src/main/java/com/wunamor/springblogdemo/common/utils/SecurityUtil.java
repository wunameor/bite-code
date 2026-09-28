package com.wunamor.springblogdemo.common.utils;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SecurityUtil {

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
}
