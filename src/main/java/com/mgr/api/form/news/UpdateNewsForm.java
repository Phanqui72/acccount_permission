package com.mgr.api.form.news;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateNewsForm {
    @NotNull(message = "id is required")
    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @NotEmpty(message = "title is required")
    @ApiModelProperty(name = "title", required = true)
    private String title;

    @NotEmpty(message = "content is required")
    @ApiModelProperty(name = "content", required = true)
    private String content;

    @NotNull(message = "categoryId is required")
    @ApiModelProperty(name = "categoryId", required = true)
    private Long categoryId;

    @NotNull(message = "status is required")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
}