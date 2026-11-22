package com.wunamor.lotterysystem.service.dto;

import lombok.Data;

/**
 * 抽奖请求提交结果。
 */
@Data
public class DrawRequestDTO {

    /**
     * 异步任务 id，同时作为前端轮询的 requestId
     */
    private Long requestId;

    private Long activityId;

    private Long prizeId;

    private String status;
}
