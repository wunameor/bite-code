package com.wunamor.lotterysystem.service;

import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import com.wunamor.lotterysystem.common.utils.RedisUtil;
import com.wunamor.lotterysystem.service.dto.ActivityDetailDTO;
import com.wunamor.lotterysystem.service.dto.ActivityDetailPrizeDTO;
import com.wunamor.lotterysystem.service.dto.ActivityDetailUserDTO;
import com.wunamor.lotterysystem.service.impl.ActivityCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActivityCacheServiceImplTest {

    private static final String DETAIL_KEY = "lottery:activity:detail:v3:100";

    private RedisUtil redisUtil;
    private ActivityCacheServiceImpl activityCacheService;

    @BeforeEach
    void setUp() {
        redisUtil = mock(RedisUtil.class);
        activityCacheService = new ActivityCacheServiceImpl(redisUtil);
    }

    @Test
    void cacheRunningActivityWritesSnapshotWith24HourTtl() {
        when(redisUtil.set(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);

        activityCacheService.cacheActivityDetail(detail(true));

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(redisUtil).set(keyCaptor.capture(), jsonCaptor.capture(), ttlCaptor.capture(),
                eq(TimeUnit.SECONDS));
        assertEquals(DETAIL_KEY, keyCaptor.getValue());
        assertEquals(86400L, ttlCaptor.getValue());

        ActivityDetailDTO cached = JacksonUtil.readValue(jsonCaptor.getValue(),
                ActivityDetailDTO.class);
        assertEquals(100L, cached.getActivityId());
        assertEquals("年会抽奖", cached.getActivityName());
        assertTrue(cached.isValid());
        assertEquals(1, cached.getPrizes().size());
        assertEquals("iPhone", cached.getPrizes().get(0).getName());
        assertEquals("张三", cached.getUsers().get(0).getUserName());
    }

    @Test
    void cacheFinishedActivityUsesSevenDayTtl() {
        when(redisUtil.set(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);

        activityCacheService.cacheActivityDetail(detail(false));

        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(redisUtil).set(anyString(), anyString(), ttlCaptor.capture(),
                eq(TimeUnit.SECONDS));
        assertEquals(604800L, ttlCaptor.getValue());
    }

    @Test
    void cacheActivityDetailIgnoresRedisFailure() {
        when(redisUtil.set(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                .thenReturn(false);

        assertDoesNotThrow(() -> activityCacheService.cacheActivityDetail(detail(true)));
    }

    @Test
    void getActivityDetailReturnsParsedSnapshot() {
        String json = JacksonUtil.writeObjectAsString(detail(true));
        when(redisUtil.get(DETAIL_KEY)).thenReturn(json);

        ActivityDetailDTO result = activityCacheService.getActivityDetail(100L);

        assertEquals(100L, result.getActivityId());
        assertEquals(1, result.getUsers().size());
    }

    @Test
    void getActivityDetailDeletesCorruptJson() {
        when(redisUtil.get(DETAIL_KEY)).thenReturn("not-json");

        ActivityDetailDTO result = activityCacheService.getActivityDetail(100L);

        assertNull(result);
        verify(redisUtil).del(DETAIL_KEY);
    }

    @Test
    void getActivityDetailReturnsNullWhenCacheMiss() {
        when(redisUtil.get(DETAIL_KEY)).thenReturn(null);

        assertNull(activityCacheService.getActivityDetail(100L));
    }

    @Test
    void evictActivityDetailDeletesKey() {
        when(redisUtil.del(DETAIL_KEY)).thenReturn(true);

        activityCacheService.evictActivityDetail(100L);

        verify(redisUtil).del(DETAIL_KEY);
    }

    private ActivityDetailDTO detail(boolean valid) {
        ActivityDetailDTO detail = new ActivityDetailDTO();
        detail.setActivityId(100L);
        detail.setActivityName("年会抽奖");
        detail.setDescription("2026 年会");
        detail.setValid(valid);

        ActivityDetailPrizeDTO prize = new ActivityDetailPrizeDTO();
        prize.setPrizeId(10L);
        prize.setName("iPhone");
        prize.setPrizeAmount(2L);
        prize.setPrizeTier("FIRST_PRIZE");
        prize.setPrizeTierName("一等奖");
        prize.setValid(true);
        prize.setList(List.of());

        ActivityDetailUserDTO user = new ActivityDetailUserDTO();
        user.setUserId(1L);
        user.setUserName("张三");

        detail.setPrizes(List.of(prize));
        detail.setUsers(List.of(user));
        return detail;
    }
}
