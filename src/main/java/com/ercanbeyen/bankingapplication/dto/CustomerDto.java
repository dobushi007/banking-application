package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public non-sealed class CustomerDto extends BaseDto {
    String name;
    String surname;
    private String phoneNumber;
    private String email;
    private Gender gender;
    private LocalDate birthDate;
}
