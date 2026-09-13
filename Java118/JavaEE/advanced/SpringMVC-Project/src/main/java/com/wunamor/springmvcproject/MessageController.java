package com.wunamor.springmvcproject;


import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    private List<Message> messageList = new ArrayList<>();

    @GetMapping("/getList")
    public List<Message> getList() {
        return messageList;
    }

    @PostMapping(value = "/publish", produces = "application/json")
    public String publish(@RequestBody Message message) {
        if (!StringUtils.hasText(message.getFrom()) ||
                !StringUtils.hasText(message.getTo()) ||
                !StringUtils.hasText(message.getMessage())) {
            return "{\"ok\": 0}";
        }

        messageList.add(message);

        return "{\"ok\": 1}";
    }
}
