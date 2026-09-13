package com.wunamor.springmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {

    @RequestMapping("/set")
    public String set(HttpServletRequest request, String key, String value) {
        HttpSession session = request.getSession(true);
        session.setAttribute(key, value);
        return "ok";
    }

    @RequestMapping("/get")
    public String get(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(true);
        if (session.getAttribute(key) == null) {
            return "none";
        }
        return (String) session.getAttribute(key);
    }

    @RequestMapping("/header")
    public String header(HttpServletRequest request, String name) {
        String header = request.getHeader(name);
        return name + ": " + header;
    }
}
