package com.wunamor.lotterysystem.mapper.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 奖品数据库对象，对应 prize 表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("prize")
public class PrizeDO extends BaseDO {

    /**
     * 奖品名称
     */
    private String name;

    /**
     * 奖品描述
     */
    private String description;

    /**
     * 奖品价格
     */
    private BigDecimal price;

    /**
     * 奖品图片相对 URL，例如 /uploads/prize/2026/11/11/xxx.png
     */
    private String imageUrl;
}
