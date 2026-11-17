package com.wunamor.lotterysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityUserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动人员关联数据访问层。
 */
@Mapper
public interface ActivityUserMapper extends BaseMapper<ActivityUserDO> {

    /**
     * 批量写入活动人员关联。
     *
     * @param list 活动人员关联列表，调用方保证非空
     * @return 受影响行数
     */
    int insertBatch(@Param("list") List<ActivityUserDO> list);
}
