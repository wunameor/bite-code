package com.wunamor.captchademo.controller;

import com.wunamor.captchademo.constant.CaptchaConstants;
import com.wunamor.captchademo.model.CaptchaSession;
import com.wunamor.captchademo.service.CaptchaService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.servlet.Session;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 获取到 captcha 对象，里面包含创建时间，验证码对象
        CaptchaSession captcha = captchaService.getCaptcha();

        // 把验证码返回到响应流内
        captcha.getCaptcha().write(response.getOutputStream());

        // 把验证码信息存储在 session 内
        session.setAttribute(CaptchaConstants.CAPTCHA_CREATE_TIME, captcha.getCreateTime());
        session.setAttribute(CaptchaConstants.CAPTCHA_VALUE, captcha.getCaptcha().getCode());
    }

    @PostMapping("/check")
    public Boolean check(String captcha, HttpSession session) {
        if (!StringUtils.hasText(captcha)) return false;

        Long creatTime = (Long) session.getAttribute(CaptchaConstants.CAPTCHA_CREATE_TIME);
        String captchaValue = (String) session.getAttribute(CaptchaConstants.CAPTCHA_VALUE);

        return captchaService.check(captcha, creatTime, captchaValue);

    }
}
