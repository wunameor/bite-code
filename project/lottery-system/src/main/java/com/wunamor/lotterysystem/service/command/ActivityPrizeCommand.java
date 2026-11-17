package com.wunamor.lotterysystem.service.command;

import com.wunamor.lotterysystem.common.enums.PrizeTierEnum;
import lombok.Data;

/**
 * 创建活动时圈选的单个奖品命令。
 */
@Data
public class ActivityPrizeCommand {

    private Long prizeId;

    private Long prizeAmount;

    private PrizeTierEnum prizeTier;
}
