package com.wunamor.mybatisplusdemo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.wunamor.mybatisplusdemo.model.BookInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookInfoMapper extends BaseMapper<BookInfo> {

    @Select("select * from book_info ${ew.customSqlSegment}")
    List<BookInfo> selectByCustom(@Param(Constants.WRAPPER) LambdaQueryWrapper<BookInfo> queryWrapper);
}
