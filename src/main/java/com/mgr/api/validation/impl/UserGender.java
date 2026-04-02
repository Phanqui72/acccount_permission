package com.mgr.api.validation.impl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserGenderValidator.class)
@Documented
public @interface UserGender {
    String message() default "Giới tính không hợp lệ. Chỉ chấp nhận 0 (Nữ), 1 (Nam), 2 (Khác)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default false; // Cho phép để trống hay không
}