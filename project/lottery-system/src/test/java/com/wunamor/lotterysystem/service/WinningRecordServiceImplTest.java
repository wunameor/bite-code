package com.wunamor.lotterysystem.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import com.wunamor.lotterysystem.common.exception.ServiceException;
import com.wunamor.lotterysystem.mapper.WinningRecordMapper;
import com.wunamor.lotterysystem.mapper.dataobject.WinningRecordDO;
import com.wunamor.lotterysystem.service.dto.WinningRecordDTO;
import com.wunamor.lotterysystem.service.impl.WinningRecordServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 中奖记录查询服务测试。
 */
class WinningRecordServiceImplTest {

    private WinningRecordMapper winningRecordMapper;
    private WinningRecordServiceImpl service;

    @BeforeAll
    static void initTableInfo() {
        MapperBuilderAssistant assistant =
                new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, WinningRecordDO.class);
    }

    @BeforeEach
    void setUp() {
        winningRecordMapper = mock(WinningRecordMapper.class);
        service = new WinningRecordServiceImpl(winningRecordMapper);
    }

    @Test
    void showConvertsRecordsAndPrizeTierName() {
        when(winningRecordMapper.selectList(any())).thenReturn(List.of(
                record(1L, 10L, "手机", PrizeTierEnum.FIRST_PRIZE)));

        List<WinningRecordDTO> result = service.show(100L, null);

        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getWinnerName());
        assertEquals(PrizeTierEnum.FIRST_PRIZE.getDesc(),
                result.get(0).getPrizeTierName());
        verify(winningRecordMapper).selectList(any());
    }

    @Test
    void showThrowsWhenActivityIdIsMissing() {
        ServiceException e = assertThrows(ServiceException.class,
                () -> service.show(null, 10L));

        assertEquals(1044, e.getCode());
    }

    private WinningRecordDO record(Long winnerId, Long prizeId, String prizeName,
                                   PrizeTierEnum tier) {
        WinningRecordDO record = new WinningRecordDO();
        record.setId(1L);
        record.setActivityId(100L);
        record.setPrizeId(prizeId);
        record.setPrizeName(prizeName);
        record.setPrizeTier(tier.getCode());
        record.setWinnerId(winnerId);
        record.setWinnerName("张三");
        record.setWinningTime(new Date());
        return record;
    }
}
