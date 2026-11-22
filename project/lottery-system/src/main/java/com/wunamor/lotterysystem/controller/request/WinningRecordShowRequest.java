package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 中奖记录查询请求。
 */
@Data
public class WinningRecordShowRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "活动 id 不能为空")
    private Long activityId;

    /**
     * 可选；为空时返回活动全部中奖记录
     */
    private Long prizeId;
}
