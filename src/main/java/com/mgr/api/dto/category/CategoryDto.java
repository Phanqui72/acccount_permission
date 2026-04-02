package com.mgr.api.dto.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgr.api.constant.MgrConstant;
import com.mgr.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDto extends ABasicAdminDto {
    @ApiModelProperty(name = "name")
    private String name;

    @ApiModelProperty(name = "description")
    private String description;

    @JsonFormat(pattern = MgrConstant.DATE_TIME_FORMAT) // Thêm dòng này
    private LocalDateTime createdDate;

    @JsonFormat(pattern = MgrConstant.DATE_TIME_FORMAT) // Thêm dòng này
    private LocalDateTime modifiedDate;
}