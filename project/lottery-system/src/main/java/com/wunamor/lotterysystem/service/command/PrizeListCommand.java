package com.wunamor.lotterysystem.service.command;

import com.wunamor.lotterysystem.common.pojo.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 奖品列表查询命令：Service 层入参。
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class PrizeListCommand extends PageQuery {
}
