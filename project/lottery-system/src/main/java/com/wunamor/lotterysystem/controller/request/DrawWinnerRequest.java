package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 抽奖请求中的单个中奖人员。
 */
@Data
public class DrawWinnerRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "中奖人员 id 不能为空")
    private Long userId;

    /**
     * 兼容前端旧字段；落库用户名以数据库为准，不使用该值。
     */
    private String userName;
}
