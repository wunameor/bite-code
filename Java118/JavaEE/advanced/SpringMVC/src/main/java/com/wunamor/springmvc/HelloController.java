package com.wunamor.springmvc;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @RequestMapping("/v1")
    public String v1(String name) {
        return "this name is " + name;
    }

    @RequestMapping("/v2")
    public Person v2(Person person) {
        return person;
    }

    @RequestMapping("/v3")
    public String v3(Integer age) {
        return "age = " + age;
    }

    @RequestMapping(value = "/v4", method = RequestMethod.GET)
    public String v4(int age) {
        return "age = " + age;
    }

    @RequestMapping("/v5")
    public String v5(@RequestParam(value = "n", required = false) String name) {
        return "this n(name) is " + name;
    }
}
