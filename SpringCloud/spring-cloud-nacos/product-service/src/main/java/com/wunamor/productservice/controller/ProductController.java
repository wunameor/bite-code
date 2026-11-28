package com.wunamor.productservice.controller;

import com.wunamor.productservice.domain.ProductDetail;
import com.wunamor.productservice.service.ProductDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductDetailService productDetailService;

    public ProductController(ProductDetailService productDetailService) {
        this.productDetailService = productDetailService;
    }

    @GetMapping("/{productId}")
    public ProductDetail getProductById(@PathVariable Integer productId) {
        log.info("获取产品信息：productId {}", productId);
        return productDetailService.getById(productId);
    }
}
