package com.wunamor.lotterysystem.controller.request;

import com.wunamor.lotterysystem.common.pojo.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 活动列表查询请求：分页参数。
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ActivityListRequest extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
