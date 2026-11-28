package com.wunamor.productservice.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wunamor.productservice.domain.ProductDetail;
import com.wunamor.productservice.mapper.ProductDetailMapper;
import com.wunamor.productservice.service.ProductDetailService;
import org.springframework.stereotype.Service;

/**
* @author Study
* @description 针对表【product_detail(产品表)】的数据库操作Service实现
* @createDate 2026-11-26 20:01:56
*/
@Service
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail>
    implements ProductDetailService {

}




