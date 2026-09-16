package com.wunamor.captchademo.model;

import cn.hutool.captcha.LineCaptcha;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaSession {
    private LineCaptcha captcha;
    private long createTime;
}
