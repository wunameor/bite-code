package com.wunamor.lotterysystem.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.Encrypt;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
    @Select("select count(1) from user where email = #{mail}")
    Integer getUserCountByEmail(@NotBlank(message = "邮箱不能为空") String mail);

    @Select("select count(1) from user where phone_number = #{phoneNumber}")
    Integer getUserCountByPhone(@Param("phoneNumber") Encrypt phoneNumber);

    /**
     * 按用户名查询用户（登录用）。
     * <p>
     * user_name 已有唯一索引 uk_user_name，这里不再加 limit 1：
     * 万一数据重复，宁可直接报错，也不要静默登进另一个人的账号。
     */
    @Select("select * from user where user_name = #{userName}")
    UserDO getUserByUserName(@Param("userName") String userName);

    /**
     * 按邮箱查询用户（登录用）。email 已有唯一索引 uk_email
     */
    @Select("select * from user where email = #{email}")
    UserDO getUserByEmail(@Param("email") String email);

    /**
     * 用户名是否已被占用（注册校验用）
     */
    @Select("select count(1) from user where user_name = #{userName}")
    Integer getUserCountByUserName(@Param("userName") String userName);

    /**
     * 人员列表：只查展示需要的列，避免把密码、手机号带出来；按 id 倒序
     */
    @Select("select id, user_name, identity from user order by id desc")
    List<UserDO> listUsers();

}
