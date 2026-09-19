package com.wunamor.bookdemo.mapper;

import com.wunamor.bookdemo.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select user_name, password from user_info where user_name = #{userName}")
    public UserInfo getUserByUserName(String userName);
}
