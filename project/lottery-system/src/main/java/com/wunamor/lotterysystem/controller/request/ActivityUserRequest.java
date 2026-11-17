package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建活动时圈选的单个人员。
 * 只传 userId，用户名由后端查库后写入，避免前端伪造。
 */
@Data
public class ActivityUserRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "人员 id 不能为空")
    private Long userId;
}
