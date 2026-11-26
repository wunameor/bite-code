package com.wunamor.orderservice.controller;


import com.wunamor.orderservice.domain.OrderDetail;
import com.wunamor.orderservice.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/{orderId}")
    public OrderDetail getOrderDetailById(@PathVariable Integer orderId) {
        return orderDetailService.getById(orderId);
    }
}
