package com.wunamor.springblogdemo.pojo.dto;

import com.wunamor.springblogdemo.pojo.entity.BlogInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class BlogUpdateDTO {
    @NotNull(message = "作者 Id 不能为空")
    @Min(1)
    private Integer id;
    @NotBlank(message = "博客标题不能为空")
    private String title;
    @NotBlank(message = "博客正文不能为空")
    private String content;

    public BlogInfo transEntity() {
        BlogInfo blogInfo = new BlogInfo();
        BeanUtils.copyProperties(this, blogInfo);
        return blogInfo;
    }
}
