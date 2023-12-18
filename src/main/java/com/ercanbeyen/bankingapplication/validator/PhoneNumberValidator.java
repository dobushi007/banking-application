package com.ercanbeyen.bankingapplication.validator;

import com.ercanbeyen.bankingapplication.annotation.PhoneNumberRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberRequest, String> {

    @Override
    public void initialize(PhoneNumberRequest constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasLength(phoneNumber)) {
            return false;
        }

        String regex = "^(\\+90)\\d{10}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.find();
    }
}
