package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 抽奖结果中的中奖人员。
 */
@Data
public class DrawWinnerDTO {

    private Long userId;

    private String userName;
}
