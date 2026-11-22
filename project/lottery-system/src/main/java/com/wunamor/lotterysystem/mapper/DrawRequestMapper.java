package com.wunamor.lotterysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.DrawRequestDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖异步任务数据访问层。
 */
@Mapper
public interface DrawRequestMapper extends BaseMapper<DrawRequestDO> {
}
