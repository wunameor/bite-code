package com.wunamor.springmvc;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/json")
public class JSONController {

    @RequestMapping("/test")
    public String test() {
        ObjectMapper mapper = new ObjectMapper();
        Person person = new Person();
        person.setAddress("localhost");
        person.setName("zhangsan");
        person.setAge(12);
        return mapper.writeValueAsString(person);
    }

    @RequestMapping("/person")
    public String person(@RequestBody Person person) {
        return person.toString();
    }
}
