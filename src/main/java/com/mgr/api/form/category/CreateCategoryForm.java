package com.mgr.api.form.category;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class CreateCategoryForm {
    @NotEmpty(message = "name is required")
    @ApiModelProperty(name = "name", required = true)
    private String name;

    @ApiModelProperty(name = "description")
    private String description;
}