package com.mgr.api.dto.user;

import com.mgr.api.dto.ABasicAdminDto;
import com.mgr.api.dto.account.AccountDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserDto extends ABasicAdminDto {

    @ApiModelProperty(name = "account")
    private AccountDto account;

    @ApiModelProperty(name = "gender")
    private Integer gender; // 0: Female, 1: Male, 2: Other

    @ApiModelProperty(name = "birthday")
    private LocalDate birthday;
}