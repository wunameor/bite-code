package com.wunamor.lotterysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动数据访问层。
 */
@Mapper
public interface ActivityMapper extends BaseMapper<ActivityDO> {
}
