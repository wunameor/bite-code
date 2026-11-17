package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.service.dto.ActivityDetailDTO;

/**
 * 活动详情缓存服务：Redis 只保存活动详情的某一时刻快照。
 */
public interface ActivityCacheService {

    /**
     * 写入活动详情快照；Redis 不可用时只记日志，不影响业务。
     *
     * @param detail 活动详情快照
     */
    void cacheActivityDetail(ActivityDetailDTO detail);

    /**
     * 读取活动详情快照。
     *
     * @param activityId 活动 id
     * @return 缓存不存在或内容损坏时返回 null，由调用方回源数据库
     */
    ActivityDetailDTO getActivityDetail(Long activityId);

    /**
     * 删除活动详情快照；抽奖等写操作提交后调用，下次读取回源重建。
     *
     * @param activityId 活动 id
     */
    void evictActivityDetail(Long activityId);
}
