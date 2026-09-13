package com.wunamor.springmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tools.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/return")
public class ReturnController {
    // 这个是返回文件（现在一般不用了）
    @RequestMapping("/test1")
    public String test1() {
        return "/test.html";
    }

    @RequestMapping("/test2")
    @ResponseBody
    public String test2() {
        return "/test.html";
    }

    @RequestMapping("/json")
    @ResponseBody
    public String json() {
        return new ObjectMapper().writeValueAsString(new Person());
    }

    @RequestMapping("/html")
    @ResponseBody
    public String html() {
        return "<h1>html test</h1>";
    }

    @RequestMapping("/setStatus")
    @ResponseBody
    public String setStatus(HttpServletResponse response) {
        response.setStatus(404);
        return "set status 404";
    }

    @RequestMapping("/setContentType")
    @ResponseBody
    public String setContentType(HttpServletResponse response) {
//        response.setContentType("application/json");
        String ret = null;
        ret = new ObjectMapper().writeValueAsString(new Person());
//        ret = "/test.html";
        return ret;
    }
}
