package com.wunamor.lotterysystem.controller.request;

import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建活动时圈选的单个奖品。
 */
@Data
public class ActivityPrizeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "奖品 id 不能为空")
    private Long prizeId;

    @NotNull(message = "奖品数量不能为空")
    @Min(value = 1, message = "奖品数量必须大于 0")
    private Long prizeAmount;

    @NotNull(message = "奖品等级不能为空")
    private PrizeTierEnum prizeTier;
}
