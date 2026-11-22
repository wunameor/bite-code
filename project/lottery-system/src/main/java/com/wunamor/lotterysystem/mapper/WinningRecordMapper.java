package com.wunamor.lotterysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 中奖记录数据访问层。
 */
@Mapper
public interface WinningRecordMapper extends BaseMapper<WinningRecordDO> {

    /**
     * 批量写入中奖记录。
     *
     * @param list 中奖记录列表，调用方保证非空
     * @return 受影响行数
     */
    int insertBatch(@Param("list") List<WinningRecordDO> list);
}
