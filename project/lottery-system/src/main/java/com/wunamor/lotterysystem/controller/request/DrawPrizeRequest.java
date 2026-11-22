package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 抽奖请求。
 */
@Data
public class DrawPrizeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "活动 id 不能为空")
    private Long activityId;

    @NotNull(message = "奖品 id 不能为空")
    private Long prizeId;

    /**
     * 兼容前端旧字段；服务端统一使用当前时间，不信任前端传入时间。
     */
    private String winningTime;

    @NotEmpty(message = "请选择中奖人员")
    @Valid
    private List<@NotNull(message = "中奖人员不能为空") DrawWinnerRequest> winnerList;
}
