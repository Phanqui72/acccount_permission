package com.mgr.api.form.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class CreateUserForm {
    @NotEmpty(message = "username is required")
    private String username;

    @NotEmpty(message = "password is required")
    private String password;

    @NotEmpty(message = "fullName is required")
    private String fullName;

    private String email;
    private String phone;
    private Integer gender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // Thêm dòng này
    private Date birthday;
    private Long groupId; // Thường gán mặc định cho một nhóm User nhất định
}