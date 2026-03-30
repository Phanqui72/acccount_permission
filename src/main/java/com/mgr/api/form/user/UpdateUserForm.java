package com.mgr.api.form.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UpdateUserForm {
    @NotNull(message = "id is required")
    @ApiModelProperty(name = "id", required = true)
    private Long id; // id này dùng chung cho cả Account và User

    @NotEmpty(message = "fullName is required")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;

    @ApiModelProperty(name = "email")
    private String email;

    @ApiModelProperty(name = "phone")
    private String phone;

    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;

    @ApiModelProperty(name = "gender")
    private Integer gender; // 0: Nữ, 1: Nam, 2: Khác

    @ApiModelProperty(name = "birthday")
    private Date birthday;

    @NotNull(message = "status is required")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;

    @ApiModelProperty(name = "password")
    private String password; // Để trống nếu không muốn đổi mật khẩu
}