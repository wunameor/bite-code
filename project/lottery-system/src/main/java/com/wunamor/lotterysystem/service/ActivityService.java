package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.common.pojo.PageResult;
import com.wunamor.lotterysystem.service.command.ActivityCreateCommand;
import com.wunamor.lotterysystem.service.command.ActivityListCommand;
import com.wunamor.lotterysystem.service.dto.ActivityCreateDTO;
import com.wunamor.lotterysystem.service.dto.ActivityDetailDTO;
import com.wunamor.lotterysystem.service.dto.ActivityListDTO;

/**
 * 活动服务。
 */
public interface ActivityService {

    /**
     * 创建活动：活动、活动奖品、活动人员在同一事务中落库。
     *
     * @param command 活动信息、奖品列表、人员列表
     * @return 新活动 id
     */
    ActivityCreateDTO createActivity(ActivityCreateCommand command);

    /**
     * 分页查询活动列表，按 id 倒序。
     *
     * @param command 分页参数
     * @return 分页后的活动列表
     */
    PageResult<ActivityListDTO> listActivities(ActivityListCommand command);

    /**
     * 查询活动抽奖详情：优先读 Redis，未命中时回源 MySQL 并回写缓存。
     *
     * @param activityId 活动 id
     * @return 活动详情快照
     */
    ActivityDetailDTO getActivityDetail(Long activityId);

    /**
     * 查询活动抽奖详情。
     *
     * @param activityId 活动 id
     * @param fresh true 表示跳过缓存，直接读取数据库最新状态
     * @return 活动详情快照
     */
    ActivityDetailDTO getActivityDetail(Long activityId, boolean fresh);
}
