package com.wunamor.lotterysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.PrizeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 奖品数据访问层
 */
@Mapper
public interface PrizeMapper extends BaseMapper<PrizeDO> {
}
