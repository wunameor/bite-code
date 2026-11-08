package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.common.enums.EmailSceneEnum;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.utils.RedisKeyUtil;
import com.wunamor.lotterysystem.common.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EmailCodeServiceTest {

    private static final String EMAIL = "wunamor-test@outlook.com";

    @Autowired
    private EmailCodeService emailCodeService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 真实发信测试。注意：同一邮箱 60 秒内只能发一次，
     * 连续跑两次会得到 1008（验证码发送过于频繁），属于预期行为。
     */
    @Test
    void send() {
        // 清掉 60 秒发送限流键，保证测试可以连续运行
        redisUtil.del(RedisKeyUtil.emailCodeLimit(EmailSceneEnum.REGISTER, EMAIL));
        emailCodeService.sendRegisterCode(EMAIL);
    }

    /**
     * 校验逻辑测试：直接往 Redis 种码，不依赖真实发信，也不受 60 秒限流影响。
     * 验证码是随机生成的，测试里不可能预先知道，所以不能写死一个码去校验。
     */
    @Test
    void verify() {
        String key = RedisKeyUtil.emailCode(EmailSceneEnum.REGISTER, EMAIL);
        redisUtil.set(key, "833703", 300L, TimeUnit.SECONDS);

        // 正确的码：校验通过
        emailCodeService.verifyRegisterCode(EMAIL, "833703");

        // 单次有效：同一个码再用一次必须失败
        assertThrows(ServiceException.class,
                () -> emailCodeService.verifyRegisterCode(EMAIL, "833703"));
    }
}
