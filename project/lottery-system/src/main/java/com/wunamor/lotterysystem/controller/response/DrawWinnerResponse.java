package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

/**
 * 抽奖结果中的中奖人员响应。
 */
@Data
public class DrawWinnerResponse {

    private Long userId;

    private String userName;
}
