package com.wunamor.lotterysystem.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.Encrypt;
import com.wunamor.lotterysystem.mapper.dataobject.UserDO;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
    @Select("select count(1) from user where email = #{mail}")
    Integer getUserCountByEmail(@NotBlank(message = "邮箱不能为空") String mail);

    @Select("select count(1) from user where phone_number = #{phoneNumber}")
    Integer getUserCountByPhone(@Param("phoneNumber") Encrypt phoneNumber);

}
