package com.wunamor.springmvc;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/cookie")
@RestController
public class CookieController {
    @RequestMapping("/c1")
    public String c1(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + " : " + cookie.getValue());
            }
        }
        return "success";
    }

    @RequestMapping("/c2")
    public String c2(@CookieValue("name") Cookie cookie) {
        return cookie.getName() + " : " + cookie.getValue();
    }
}
