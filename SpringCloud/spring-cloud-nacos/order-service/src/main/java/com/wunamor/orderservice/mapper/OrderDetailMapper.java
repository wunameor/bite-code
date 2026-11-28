package com.wunamor.orderservice.mapper;

import com.wunamor.orderservice.domain.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Study
* @description 针对表【order_detail(订单表)】的数据库操作Mapper
* @createDate 2026-11-26 18:58:06
* @Entity generator.domain.OrderDetail
*/

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}




