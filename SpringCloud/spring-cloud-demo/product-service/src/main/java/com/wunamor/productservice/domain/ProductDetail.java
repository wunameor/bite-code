package com.wunamor.productservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 产品表
 * @TableName product_detail
 */
@TableName(value ="product_detail")
@Data
public class ProductDetail implements Serializable {
    /**
     * 产品id
     */
    @TableId(type = IdType.AUTO)
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
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
