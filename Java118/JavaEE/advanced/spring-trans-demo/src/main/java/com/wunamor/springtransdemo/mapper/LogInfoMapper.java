package com.wunamor.springtransdemo.mapper;


import com.wunamor.springtransdemo.model.LogInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogInfoMapper {

    @Insert("insert into log_info(user_name, op) values (#{userName}, #{op}) ")
    public Integer insertLog(LogInfo logInfo);
}
