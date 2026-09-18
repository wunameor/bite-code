package com.wunamor.mybatisdemo.mapper;

import com.wunamor.mybatisdemo.model.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user_info")
    public List<UserInfo> getList();

    @Results({
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "delete_flag", property = "deleteFlag"),
    })
    @Select("select * from user_info")
    public List<UserInfo> getList2();

    @Select("select * from user_info where id = #{id}")
    public List<UserInfo> getListById(Integer id);

    @Select("select * from user_info where username = #{username} and password = #{password}")
    public UserInfo getUserByUsernameAndPassword(UserInfo userInfo);

    @Select("select * from user_info where username = #{userInfo.username} and password = #{userInfo.password}")
    public UserInfo getUserByUsernameAndPassword2(@Param("userInfo") UserInfo userInfo);

    @Select("select * from user_info where username like concat('%',#{username}, '%')")
    public List<UserInfo> getUserByLikeName(String username);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user_info(username, password, age) values (#{username}, #{password}, #{age})")
    public Integer insertUser(UserInfo userInfo);

    @Delete("delete from user_info where id = #{id}")
    public Integer deleteUserById(Integer id);

    @Update("update user_info set username = #{username} where id = #{id}")
    public Integer updateUserById(UserInfo userInfo);
}
