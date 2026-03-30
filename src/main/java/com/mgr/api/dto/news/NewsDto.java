package com.mgr.api.dto.news;

import com.mgr.api.dto.ABasicAdminDto;
import com.mgr.api.dto.category.CategoryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewsDto extends ABasicAdminDto {
    @ApiModelProperty(name = "title")
    private String title;

    @ApiModelProperty(name = "content")
    private String content;

    @ApiModelProperty(name = "category")
    private CategoryDto category;
}