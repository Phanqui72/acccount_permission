package com.mgr.api.validation.impl;

import com.mgr.api.constant.MgrConstant;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class UserGenderValidator implements ConstraintValidator<UserGender, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(UserGender constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return allowNull;
        }
        // Kiểm tra giá trị có nằm trong bộ [0, 1, 2] không
        return Objects.equals(value, MgrConstant.GENDER_FEMALE) ||
                Objects.equals(value, MgrConstant.GENDER_MALE) ||
                Objects.equals(value, MgrConstant.GENDER_OTHER);
    }
}