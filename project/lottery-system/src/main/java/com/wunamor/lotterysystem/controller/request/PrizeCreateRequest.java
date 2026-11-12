package com.wunamor.lotterysystem.controller.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建奖品请求：multipart 表单字段直接绑定到这个对象。
 */
@Data
public class PrizeCreateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "奖品名称不能为空")
    @Size(max = 255, message = "奖品名称不能超过 255 个字符")
    private String prizeName;

    @Size(max = 255, message = "奖品描述不能超过 255 个字符")
    private String description;

    @NotNull(message = "奖品价格不能为空")
    @DecimalMin(value = "0.01", message = "奖品价格必须大于 0")
    @DecimalMax(value = "99999999.99", message = "奖品价格不能超过 99999999.99")
    private BigDecimal price;
}
