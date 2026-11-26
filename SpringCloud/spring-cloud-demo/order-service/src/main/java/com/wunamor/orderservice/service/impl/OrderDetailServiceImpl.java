package com.wunamor.orderservice.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wunamor.orderservice.domain.OrderDetail;
import com.wunamor.orderservice.service.OrderDetailService;
import com.wunamor.orderservice.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author Study
* @description 针对表【order_detail(订单表)】的数据库操作Service实现
* @createDate 2026-11-26 18:58:06
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




