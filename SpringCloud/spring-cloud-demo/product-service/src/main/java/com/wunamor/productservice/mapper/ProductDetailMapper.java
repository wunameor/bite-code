package com.wunamor.productservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wunamor.productservice.domain.ProductDetail;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Study
* @description 针对表【product_detail(产品表)】的数据库操作Mapper
* @createDate 2026-11-26 20:01:56
* @Entity generator.domain.ProductDetail
*/
@Mapper
public interface ProductDetailMapper extends BaseMapper<ProductDetail> {

}




