package com.wunamor.springiocdemo.controller;


import com.wunamor.springiocdemo.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proper")
public class PropertiesController {

    @Autowired
    private Student student;

    @Value("${student.gender}")
    private int gender;

    @RequestMapping("/r1")
    public Student r1() {
        System.out.println(student);
        System.out.println(gender);
        return null;
//        return student;
    }
}
