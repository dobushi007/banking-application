package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.annotation.PhoneNumberRequest;
import com.ercanbeyen.bankingapplication.constant.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public non-sealed class CustomerDto extends BaseDto {
    @NotBlank(message = "Name should not be blank")
    @Size(min = 3, max = 100, message = "Length of name is not between {min} and {max}")
    String name;
    @NotBlank(message = "Surname should not be blank")
    @Size(min = 2, max = 100, message = "Length of surname is not between {min} and {max}")
    String surname;
    @NotBlank(message = "National identity should not be blank")
    @Pattern(regexp = "\\d{11}", message = "Length of national identity is not 11")
    private String nationalId;
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
