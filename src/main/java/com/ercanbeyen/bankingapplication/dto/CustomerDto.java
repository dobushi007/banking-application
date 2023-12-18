package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.annotation.PhoneNumberRequest;
import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public non-sealed class CustomerDto extends BaseDto {
    @NotBlank(message = "Name should not be blank")
    String name;
    @NotBlank(message = "Name should not be blank")
    String surname;
    @PhoneNumberRequest
    private String phoneNumber;
    @NotBlank(message = "Email should not be blank")
    @Email(message = "Invalid email")
    private String email;
    @NotNull(message = "Gender should not be null")
    private Gender gender;
    @NotNull(message = "Birth date should not be null")
    private LocalDate birthDate;
    @Valid // For nested validations
    private AddressDto addressDto;
}
