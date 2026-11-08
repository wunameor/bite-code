package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.common.enums.EmailSceneEnum;

/**
 * 邮箱验证码服务：生成、发送、校验
 */
public interface EmailCodeService {

    /**
     * 发送验证码
     *
     * @param email 收件邮箱
     * @param scene 使用场景（注册/登录/重置密码），不同场景的验证码互不影响
     */
    void sendCode(String email, EmailSceneEnum scene);

    /**
     * 校验验证码，校验通过后验证码立即失效（单次有效）
     *
     * @param email 收件邮箱
     * @param code  用户填写的验证码
     * @param scene 使用场景
     */
    void verifyCode(String email, String code, EmailSceneEnum scene);

    // ==================== 注册场景的便捷方法 ====================

    default void sendRegisterCode(String email) {
        sendCode(email, EmailSceneEnum.REGISTER);
    }

    default void verifyRegisterCode(String email, String code) {
        verifyCode(email, code, EmailSceneEnum.REGISTER);
    }
}
