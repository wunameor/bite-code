package com.wunamor.springmvc;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

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

    @RequestMapping("/student/{id}")
    public String student(@PathVariable String id) {
        if ("1".equals(id)) {
            return "zhangsan";
        } else if ("2".equals(id)) {
            return  "lisi";
        }
        return "error";
    }

    @RequestMapping("/file")
    public String file(@RequestPart MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        File newFile = new File("E:\\test\\java\\" + file.getOriginalFilename());
        file.transferTo(newFile);
        return "success";
    }
}
