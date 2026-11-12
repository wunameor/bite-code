package com.wunamor.lotterysystem.common.pojo;

import com.wunamor.lotterysystem.common.utils.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageResultTest {

    @Test
    void totalPagesRoundsUp() {
        PageResult<String> result = new PageResult<>(List.of("a"), 11, 1, 5);
        assertEquals(3, result.getTotalPages());
    }

    @Test
    void totalPagesIsExactWhenDivisible() {
        PageResult<String> result = new PageResult<>(List.of("a"), 10, 1, 5);
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void totalPagesIsZeroWhenEmpty() {
        PageResult<String> result = new PageResult<>(List.of(), 0, 1, 10);
        assertEquals(0, result.getTotalPages());
    }

    @Test
    void totalPagesIsZeroWhenPageSizeIsZero() {
        PageResult<String> result = new PageResult<>(List.of(), 10, 1, 0);
        assertEquals(0, result.getTotalPages());
    }

    @Test
    void serializesTotalPages() {
        PageResult<String> result = new PageResult<>(List.of("a"), 11, 1, 5);
        String json = JacksonUtil.writeObjectAsString(result);
        assertTrue(json.contains("\"totalPages\":3"), json);
    }
}
