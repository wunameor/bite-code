package com.wunamor.captchademo.service;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Console;
import com.wunamor.captchademo.configuration.CaptchaConfiguration;
import com.wunamor.captchademo.model.CaptchaSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

    @Autowired
    private CaptchaConfiguration captchaConfiguration;

    public CaptchaSession getCaptcha() {
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(captchaConfiguration.getWidth(),
                captchaConfiguration.getHeight());
        //输出code
        Console.log(lineCaptcha.getCode());

        return new CaptchaSession(lineCaptcha, System.currentTimeMillis());
    }

    public Boolean check(String captcha, Long creatTime, String captchaValue) {
        // 这里其实是要放在 service 里面，但是这里是学习，就不放了
        if (creatTime == null || captchaValue == null) return false;

        if (System.currentTimeMillis() - creatTime > captchaConfiguration.getTTL() ||
                !captcha.equals(captchaValue)) {
            return false;
        }

        return true;
    }
}
