package com.wunamor.springmvcproject.controller;


import com.wunamor.springmvcproject.model.Message;
import com.wunamor.springmvcproject.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    private List<Message> messageList = new ArrayList<>();

    @Autowired
    private MessageService messageService;

    @GetMapping("/getList")
    public List<Message> getList() {
//        return messageList;
        return messageService.getList();
    }

    @PostMapping(value = "/publish", produces = "application/json")
    public String publish(@RequestBody Message message) {
        if (!StringUtils.hasText(message.getFrom()) ||
                !StringUtils.hasText(message.getTo()) ||
                !StringUtils.hasText(message.getMessage())) {
            return "{\"ok\": 0}";
        }

        Integer result = messageService.insertMessage(message);
//        messageList.add(message);

        return "{\"ok\": 1}";
    }
}
