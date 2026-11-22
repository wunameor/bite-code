package com.wunamor.lotterysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.ActivityDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动数据访问层。
 */
@Mapper
public interface ActivityMapper extends BaseMapper<ActivityDO> {

    /**
     * 在事务中锁定活动行；同一活动的抽奖请求会串行执行。
     *
     * @param id 活动 id
     * @return 活动数据，不存在时返回 null
     */
    @Select("SELECT id, activity_name, description, status, gmt_create, gmt_modified "
            + "FROM activity WHERE id = #{id} FOR UPDATE")
    ActivityDO selectByIdForUpdate(@Param("id") Long id);
}
