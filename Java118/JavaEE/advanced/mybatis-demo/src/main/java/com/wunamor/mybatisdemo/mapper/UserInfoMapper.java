package com.wunamor.mybatisdemo.mapper;

import com.wunamor.mybatisdemo.model.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    public List<UserInfo> getList();


//    public List<UserInfo> getList2();

    public List<UserInfo> getListById(Integer id);

    public UserInfo getUserByUsernameAndPassword(UserInfo userInfo);

    public UserInfo getUserByUsernameAndPassword2(@Param("userInfo") UserInfo userInfo);

    public Integer insertUser(UserInfo userInfo);

    public Integer deleteUserById(Integer id);

    public Integer updateUserById(UserInfo userInfo);
}
