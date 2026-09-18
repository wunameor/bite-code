package com.wunamor.springmvcproject.service;


import com.wunamor.springmvcproject.mapper.MessageMapper;
import com.wunamor.springmvcproject.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public List<Message> getList() {
        return messageMapper.getList();
    }

    public Integer insertMessage(Message message) {
        return messageMapper.insertMessage(message);
    }
}
