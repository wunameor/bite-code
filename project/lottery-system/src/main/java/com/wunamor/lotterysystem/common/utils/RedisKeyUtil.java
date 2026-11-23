package com.wunamor.lotterysystem.common.utils;

import com.wunamor.lotterysystem.common.enums.EmailSceneEnum;

/**
 * Redis key 统一生成入口：业务代码不再手写拼接字符串。
 * <p>
 * 命名规范：{项目}:{业务}:{类型}:{场景}:{标识}
 * 例如 lottery:email:code:register:123@qq.com
 */
public final class RedisKeyUtil {

    /**
     * 项目前缀，多个项目共用一个 Redis 时用于隔离
     */
    private static final String PREFIX = "lottery";

    private RedisKeyUtil() {
    }

    private static String join(String... parts) {
        return PREFIX + ":" + String.join(":", parts);
    }

    /**
     * 邮箱验证码：lottery:email:code:register:123@qq.com
     */
    public static String emailCode(EmailSceneEnum scene, String email) {
        return join("email", "code", scene.getCode(), email);
    }

    /**
     * 邮箱验证码发送限流：lottery:email:code:limit:register:123@qq.com
     */
    public static String emailCodeLimit(EmailSceneEnum scene, String email) {
        return join("email", "code", "limit", scene.getCode(), email);
    }

    /**
     * 活动详情快照：lottery:activity:detail:v3:24
     * v3 用于隔离不含中奖名单的旧缓存。
     */
    public static String activityDetail(Long activityId) {
        return join("activity", "detail", "v3", String.valueOf(activityId));
    }
}
