package com.wunamor.lotterysystem.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // ====================== string ======================
    public boolean set(String key, String value, Long time, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, time, timeUnit);
            return true;
        } catch (Exception e) {
            log.error("RedisUtil 出现异常-set key: {}, value: {}, time: {}, timeUnit: {}",
                    key, value, time, timeUnit);
            return false;
        }
    }

    public boolean set(String key, String value, Long time) {
        return set(key, value, time, TimeUnit.SECONDS);
    }

    public boolean set(String key, String value) {
        return set(key, value, 0L, TimeUnit.SECONDS);
    }

    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("RedisUtil 出现异常-get key: {}", key);
            return null;
        }
    }

    /**
     * 原子操作：key 不存在时才写入并设置过期时间（分布式限流/防重复提交）
     * <p>
     * 注意：这里 Redis 异常必须抛出，不能返回 false。否则“Redis 挂了”会被调用方
     * 误判成“key 已存在”（例如验证码接口会错误提示“发送过于频繁”）。
     *
     * @return true 抢到锁（写入成功），false key 已存在
     */
    public boolean setIfAbsent(String key, String value, Long time, TimeUnit timeUnit) {
        try {
            return Boolean.TRUE.equals(
                    stringRedisTemplate.opsForValue().setIfAbsent(key, value, time, timeUnit));
        } catch (Exception e) {
            log.error("RedisUtil 出现异常-setIfAbsent key: {}", key, e);
            throw new IllegalStateException("Redis 不可用，无法执行 setIfAbsent，key: " + key, e);
        }
    }

    // ====================== global ======================
    public boolean del(String... keys) {
        if (keys == null) return false;
        try {
            stringRedisTemplate.delete(List.of(keys));
            return true;
        } catch (Exception e) {
            log.error("RedisUtil 出现异常-get keys: {}", (Object) keys);
            return false;
        }
    }

    public boolean exists(String key) {

        try {
            return stringRedisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("RedisUtil 出现异常-get exists: {}", key);
            return false;
        }
    }
}
