package com.wunamor.lotterysystem.controller.response;

import lombok.Data;

/**
 * 抽奖请求提交响应。
 */
@Data
public class DrawPrizeResponse {

    /**
     * 异步任务 id，前端用它轮询结果
     */
    private Long requestId;

    private Long activityId;

    private Long prizeId;

    private String status;
}
