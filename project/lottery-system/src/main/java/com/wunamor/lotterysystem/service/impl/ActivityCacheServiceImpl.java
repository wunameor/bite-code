package com.wunamor.lotterysystem.service.impl;

import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.common.utils.RedisKeyUtil;
import com.wunamor.lotterysystem.common.utils.RedisUtil;
import com.wunamor.lotterysystem.service.ActivityCacheService;
import com.wunamor.lotterysystem.service.dto.ActivityDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 活动详情缓存实现：一个活动一个 String key，value 是完整 JSON 快照。
 */
@Slf4j
@Service
public class ActivityCacheServiceImpl implements ActivityCacheService {

    /**
     * 进行中活动缓存 24 小时，抽奖后删除 key，TTL 只做兜底
     */
    private static final long RUNNING_TTL_SECONDS = 24L * 60 * 60;

    /**
     * 已结束活动基本不再变化，缓存 7 天
     */
    private static final long FINISHED_TTL_SECONDS = 7L * 24 * 60 * 60;

    private final RedisUtil redisUtil;

    public ActivityCacheServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public void cacheActivityDetail(ActivityDetailDTO detail) {
        // 1. 快照或活动 id 为空时没有可写入的内容
        if (detail == null || detail.getActivityId() == null) {
            log.warn("活动详情为空，跳过缓存写入");
            return;
        }

        String key = RedisKeyUtil.activityDetail(detail.getActivityId());
        try {
            // 2. 整对象序列化，按活动是否进行中选择固定 TTL
            String json = JacksonUtil.writeObjectAsString(detail);
            long ttl = detail.isValid() ? RUNNING_TTL_SECONDS : FINISHED_TTL_SECONDS;

            // 3. Redis 写入失败只记日志，不影响活动创建或详情查询
            boolean success = redisUtil.set(key, json, ttl, TimeUnit.SECONDS);
            if (!success) {
                log.warn("活动详情缓存写入失败，activityId: {}", detail.getActivityId());
            }
        } catch (RuntimeException e) {
            log.error("活动详情缓存序列化失败，activityId: {}", detail.getActivityId(), e);
        }
    }

    @Override
    public ActivityDetailDTO getActivityDetail(Long activityId) {
        // 1. 活动 id 为空时直接视为未命中
        if (activityId == null) {
            return null;
        }

        // 2. 读取缓存；未命中时返回 null，由调用方回源数据库
        String key = RedisKeyUtil.activityDetail(activityId);
        String json = redisUtil.get(key);
        if (!StringUtils.hasText(json)) {
            return null;
        }

        try {
            // 3. 反序列化完整快照
            return JacksonUtil.readValue(json, ActivityDetailDTO.class);
        } catch (RuntimeException e) {
            // 4. 缓存损坏时删除，避免后续请求持续解析失败
            log.warn("活动详情缓存内容损坏，activityId: {}", activityId, e);
            redisUtil.del(key);
            return null;
        }
    }

    @Override
    public void evictActivityDetail(Long activityId) {
        // 1. 活动 id 为空时无需删除
        if (activityId == null) {
            return;
        }

        // 2. 删除整个快照，下次读取回源重建，避免局部字段不同步
        String key = RedisKeyUtil.activityDetail(activityId);
        boolean success = redisUtil.del(key);
        if (!success) {
            log.warn("活动详情缓存删除失败，activityId: {}", activityId);
        }
    }
}
