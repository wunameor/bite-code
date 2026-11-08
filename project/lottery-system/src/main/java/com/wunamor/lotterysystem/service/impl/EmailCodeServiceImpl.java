package com.wunamor.lotterysystem.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.wunamor.lotterysystem.common.enums.EmailSceneEnum;
import com.wunamor.lotterysystem.common.errorcode.ServiceErrorCodeConstants;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.common.utils.MailUtil;
import com.wunamor.lotterysystem.common.utils.RedisKeyUtil;
import com.wunamor.lotterysystem.common.utils.RedisUtil;
import com.wunamor.lotterysystem.common.utils.RegexUtil;
import com.wunamor.lotterysystem.service.EmailCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailCodeServiceImpl implements EmailCodeService {

    /**
     * 验证码长度：6 位纯数字
     */
    private static final int CODE_LENGTH = 6;

    /**
     * 验证码有效期：5 分钟
     */
    private static final long CODE_TTL_SECONDS = 5 * 60L;

    /**
     * 重发间隔：60 秒
     */
    private static final long LIMIT_TTL_SECONDS = 60L;

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void sendCode(String email, EmailSceneEnum scene) {
        // 1. 格式校验
        if (!RegexUtil.checkMail(email)) {
            throw new ServiceException(ServiceErrorCodeConstants.REGISTER_MAIL_PATTERN_ERROR);
        }

        // 2. 限流：SET NX EX，两个并发请求只有一个能拿到
        String limitKey = RedisKeyUtil.emailCodeLimit(scene, email);
        if (!redisUtil.setIfAbsent(limitKey, "1", LIMIT_TTL_SECONDS, TimeUnit.SECONDS)) {
            throw new ServiceException(ServiceErrorCodeConstants.EMAIL_CODE_SEND_FREQUENT);
        }

        // 3. Hutool 生成 6 位数字验证码（允许前导 0），存入 Redis
        String code = RandomUtil.randomNumbers(CODE_LENGTH);
        String codeKey = RedisKeyUtil.emailCode(scene, email);
        if (!redisUtil.set(codeKey, code, CODE_TTL_SECONDS, TimeUnit.SECONDS)) {
            // Redis 写失败不能只发邮件，否则用户永远验不过
            throw new ServiceException(ServiceErrorCodeConstants.EMAIL_SEND_ERROR);
        }

        // 4. 发送
        try {
            mailUtil.sendText(email, buildSubject(scene), buildContent(scene, code));
        } catch (Exception e) {
            // 发送失败要把验证码和限流键清掉，允许用户立即重试
            redisUtil.del(codeKey, limitKey);
            log.error("{}验证码邮件发送失败，email: {}，原因: {}"
                            + "（请检查 MAIL_USERNAME / MAIL_PASSWORD 与 SMTP 配置）",
                    scene.getDesc(), email, e.getMessage(), e);
            throw new ServiceException(ServiceErrorCodeConstants.EMAIL_SEND_ERROR);
        }
    }

    @Override
    public void verifyCode(String email, String code, EmailSceneEnum scene) {
        String codeKey = RedisKeyUtil.emailCode(scene, email);
        String cached = redisUtil.get(codeKey);
        if (cached == null || !cached.equals(code)) {
            throw new ServiceException(ServiceErrorCodeConstants.EMAIL_CODE_ERROR);
        }
        // 单次有效：校验通过就删除，避免一个码被用多次
        redisUtil.del(codeKey);
    }

    static String buildSubject(EmailSceneEnum scene) {
        return "【抽奖系统】" + scene.getDesc() + "验证码";
    }

    /**
     * 邮件正文：验证码独占一行，前后留空行。
     * <p>
     * 邮件客户端按“空白”判断词边界，这样鼠标双击只会选中 6 位数字，
     * 不会把后面的“，5”一起选中。
     */
    static String buildContent(EmailSceneEnum scene, String code) {
        return "您的" + scene.getDesc() + "验证码是：\n\n"
                + "    " + code + "\n\n"
                + (CODE_TTL_SECONDS / 60) + " 分钟内有效。如非本人操作，请忽略本邮件。";
    }
}
