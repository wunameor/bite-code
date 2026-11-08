package com.wunamor.lotterysystem.common.utils;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * 通用邮件发送工具：只负责“怎么发出去”，不含任何业务逻辑。
 * <p>
 * 发送失败会抛出 Spring 的 {@code MailException}（运行时异常），由调用方决定如何转换。
 */
@Slf4j
@Component
public class MailUtil {

    private final JavaMailSender mailSender;

    /**
     * 发件人地址，QQ/163 要求必须与登录账号一致，否则会被拒收
     */
    private final String from;

    public MailUtil(JavaMailSender mailSender,
                    @Value("${spring.mail.username:}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    /**
     * 启动时提前暴露配置问题：发件人为空只影响邮件功能，不阻塞应用启动
     */
    @PostConstruct
    public void checkConfig() {
        if (!StringUtils.hasText(from)) {
            log.warn("未配置发件邮箱（spring.mail.username / 环境变量 MAIL_USERNAME），邮件功能不可用");
        } else {
            log.info("邮件发件人：{}", from);
        }
    }

    /**
     * 发送纯文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 正文
     */
    public void sendText(String to, String subject, String content) {
        sendText(new String[]{to}, subject, content);
    }

    /**
     * 发送纯文本邮件（多收件人）
     */
    public void sendText(String[] to, String subject, String content) {
        checkFromConfigured();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
        log.info("文本邮件发送成功，to: {}, subject: {}", String.join(",", to), subject);
    }

    /**
     * 发送 HTML 邮件（如中奖通知、活动图文）
     *
     * @param to      收件人
     * @param subject 主题
     * @param html    HTML 正文
     */
    public void sendHtml(String to, String subject, String html) {
        checkFromConfigured();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("HTML 邮件发送成功，to: {}, subject: {}", to, subject);
        } catch (MessagingException e) {
            // 包装成运行时异常，避免调用方到处 try-catch
            throw new IllegalStateException("构建邮件内容失败，to: " + to, e);
        }
    }

    /**
     * 发件人为空时，JavaMail 会抛出很难理解的
     * {@code AddressException: Illegal address in string ''}，
     * 这里提前拦截，直接给出可操作的提示。
     */
    private void checkFromConfigured() {
        if (!StringUtils.hasText(from)) {
            throw new IllegalStateException(
                    "未配置发件邮箱：请设置环境变量 MAIL_USERNAME=你的QQ邮箱地址，"
                            + "或填写 application.yaml 中的 spring.mail.username");
        }
    }
}
