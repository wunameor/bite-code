package com.wunamor.orderservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 订单服务调用 product-service 时使用的商品 DTO。
 * 字段与 product-service 的 ProductDetail 保持一致，避免跨模块直接依赖实体类。
 */
@Data
public class ProductDTO {

    /**
     * 产品id
     */
    private Integer id;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品价格
     */
    private Long productPrice;

    /**
     * 产品状态 0-有效 1-下架
     */
    private Integer state;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
