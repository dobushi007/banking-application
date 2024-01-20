package com.ercanbeyen.bankingapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NotificationDto(
        @NotBlank(message = "National identity should not be blank")
        @Pattern(regexp = "\\d{11}", message = "Length of national identity must be 11 characters")
        String customerNationalId,
        @NotBlank(message = "Message should not be blank")
        String message) {

}
