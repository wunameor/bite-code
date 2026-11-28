package com.wunamor.orderservice.controller;


import com.wunamor.orderservice.domain.OrderDetail;
import com.wunamor.orderservice.domain.ProductDTO;
import com.wunamor.orderservice.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 带负载均衡能力的 RestTemplate，由 BeanConfig 创建
     */
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/{orderId}")
    public OrderDetail getOrderDetailById(@PathVariable Integer orderId) {
        return orderDetailService.getById(orderId);
    }

    /**
     * 根据订单 ID 查询订单对应的商品信息。
     * 通过 Nacos 服务名 product-service 发起调用，由 LoadBalancer 选择实例。
     *
     * @param orderId 订单 ID
     * @return 商品信息；订单不存在或订单未关联商品时返回 null
     */
    @GetMapping("/{orderId}/product")
    public ProductDTO getProductByOrderId(@PathVariable Integer orderId) {
        log.info("获取订单对应的商品：orderId {}", orderId);
        // 先查订单，拿到商品 ID
        OrderDetail order = orderDetailService.getById(orderId);
        if (order == null || order.getProductId() == null) {
            return null;
        }
        // 使用服务名调用 product-service，避免写死 IP 和端口
        return restTemplate.getForObject(
                "http://product-service/product/{productId}",
                ProductDTO.class,
                order.getProductId());
    }
}
