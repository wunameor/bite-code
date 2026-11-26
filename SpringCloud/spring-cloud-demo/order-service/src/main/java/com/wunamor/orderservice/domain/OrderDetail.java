package com.wunamor.orderservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 订单表
 * @TableName order_detail
 */
@TableName(value ="order_detail")
@Data
public class OrderDetail {
    /**
     * 订单id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 下单数量
     */
    private Integer num;

    /**
     * 实付款
     */
    private Long price;

    /**
     *
     */
    private Integer deleteFlag;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;
}
