package com.wunamor.mybatisdemo.model;


import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {
    private int id;
    private String username;
    private String password;
    private byte age;
    private byte gender;
    private String phone;
    private byte deleteFlag;
    private Date creatTime;
    private Date updateTime;
}
