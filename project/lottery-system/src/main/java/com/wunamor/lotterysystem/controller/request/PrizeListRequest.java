package com.wunamor.lotterysystem.controller.request;

import com.wunamor.lotterysystem.common.pojo.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 奖品列表查询请求：分页参数。
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class PrizeListRequest extends PageQuery {
}
