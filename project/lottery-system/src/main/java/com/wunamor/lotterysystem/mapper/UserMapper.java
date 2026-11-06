package com.wunamor.lotterysystem.mapper;


import com.wunamor.lotterysystem.mapper.dataobject.Encrypt;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select count(1) from user where email = #{mail}")
    Integer getUserCountByEmail(@NotBlank(message = "邮箱不能为空") String mail);

    @Select("select count(1) from user where phone_number = #{phoneNumber}")
    Integer getUserCountByPhone(Encrypt phoneNumber);

}
