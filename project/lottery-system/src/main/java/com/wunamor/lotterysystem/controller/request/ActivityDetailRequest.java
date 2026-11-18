package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 活动详情查询请求：只需要活动 id。
 */
@Data
public class ActivityDetailRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "活动 id 不能为空")
    @Min(value = 1, message = "活动 id 必须大于 0")
    private Long activityId;
}
