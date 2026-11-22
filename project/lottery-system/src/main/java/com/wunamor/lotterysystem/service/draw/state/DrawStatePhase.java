package com.wunamor.lotterysystem.service.draw.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抽奖状态翻转阶段。
 *
 * <p>阶段用于表达业务上的先后依赖，phase 数值只用于排序，不表示数据库状态。</p>
 */
@Getter
@AllArgsConstructor
public enum DrawStatePhase {

    RECORD(100),
    PRIZE(200),
    ACTIVITY_USER(300),
    ACTIVITY(400),
    DRAW_REQUEST(500),
    ;

    /**
     * 排序权重，数值越小越先执行。
     */
    private final int index;
}
