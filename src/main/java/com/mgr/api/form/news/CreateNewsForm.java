package com.mgr.api.form.news;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateNewsForm {
    @NotEmpty(message = "title is required")
    private String title;

    @NotEmpty(message = "content is required")
    private String content;

    @NotNull(message = "categoryId is required")
    private Long categoryId;
}