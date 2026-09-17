package com.wunamor.mybatisdemo.model;


import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {
    private int id;
    private String username;
    private String password;
    private int age;
    private int gender;
    private String phone;
    private int deleteFlag;
    private Date createTime;
    private Date updateTime;
}
