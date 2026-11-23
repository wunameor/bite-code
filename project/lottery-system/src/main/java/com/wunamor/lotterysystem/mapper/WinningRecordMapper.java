package com.wunamor.lotterysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 统计某个活动奖品已经抽出的中奖人数，用于判断奖品是否抽满。
     *
     * @param activityId 活动 id
     * @param prizeId    奖品 id
     * @return 已中奖人数，没有记录时返回 0
     */
    @Select("SELECT COUNT(*) FROM winning_record "
            + "WHERE activity_id = #{activityId} AND prize_id = #{prizeId}")
    long countByActivityIdAndPrizeId(@Param("activityId") Long activityId,
                                     @Param("prizeId") Long prizeId);

    /**
     * 查询一个活动的全部中奖记录，用于组装活动详情缓存快照。
     *
     * @param activityId 活动 id
     * @return 按中奖时间升序排列的中奖记录
     */
    @Select("SELECT * FROM winning_record WHERE activity_id = #{activityId} "
            + "ORDER BY winning_time ASC, id ASC")
    List<WinningRecordDO> listByActivityId(@Param("activityId") Long activityId);
}
