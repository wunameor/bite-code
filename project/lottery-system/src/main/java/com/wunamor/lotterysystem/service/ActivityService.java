package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.common.pojo.PageResult;
import com.wunamor.lotterysystem.service.command.ActivityCreateCommand;
import com.wunamor.lotterysystem.service.command.ActivityListCommand;
import com.wunamor.lotterysystem.service.dto.ActivityCreateDTO;
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
}
