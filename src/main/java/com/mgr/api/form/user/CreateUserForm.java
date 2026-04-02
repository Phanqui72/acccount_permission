package com.mgr.api.form.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mgr.api.validation.impl.UserGender;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

@Data
public class CreateUserForm {
    @ApiModelProperty(value = "Tên đăng nhập", required = true, example = "nguyenvana")
    @NotEmpty(message = "username is required")
    private String username;

    @ApiModelProperty(value = "Mật khẩu", required = true, example = "123456Abc@")
    @NotEmpty(message = "password is required")
    private String password;

    @ApiModelProperty(value = "Họ và tên đầy đủ", required = true, example = "Nguyễn Văn A")
    @NotEmpty(message = "fullName is required")
    private String fullName;

    @ApiModelProperty(value = "Địa chỉ Email", example = "vana@gmail.com")
    @Email(message = "invalid email format")
    private String email;

    @ApiModelProperty(value = "Số điện thoại", example = "0987654321")
    private String phone;


    @NotNull(message = "gender is required")
    @UserGender(message = "Gender invalid! 0: Female, 1: Male, 2: Other")
    @ApiModelProperty(value = "Giới tính (0: Nữ, 1: Nam, 2: Khác)", required = true, allowableValues = "0,1,2", example = "1")
    private Integer gender;

    @ApiModelProperty(value = "Ngày sinh (định dạng yyyy-MM-dd)", example = "1995-05-20")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // Thêm dòng này
    private LocalDate birthday;

    @NotNull(message = "groupId is required")
    @ApiModelProperty(value = "ID của nhóm quyền (Group)", required = true, example = "1")
    private Long groupId; // Thường gán mặc định cho một nhóm User nhất định
}