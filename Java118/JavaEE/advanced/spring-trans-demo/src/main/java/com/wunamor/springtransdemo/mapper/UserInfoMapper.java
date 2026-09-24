package com.wunamor.springtransdemo.mapper;


import com.wunamor.springtransdemo.model.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper {
    @Insert("insert into user_info(user_name, password) values (#{userName}, #{password})")
    Integer insertUser(UserInfo userInfo);
}
