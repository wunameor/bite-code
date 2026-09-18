package com.wunamor.springmvcproject.mapper;


import com.wunamor.springmvcproject.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {
    @Select("select * from message_info")
    public List<Message> getList();

    @Insert("insert into message_info(`from`, `to`, `message`) values (#{from}, #{to}, #{message})")
    public Integer insertMessage(Message message);
}
