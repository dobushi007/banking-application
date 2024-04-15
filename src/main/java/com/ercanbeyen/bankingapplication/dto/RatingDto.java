package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.RatingReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.UUID;

public record RatingDto(
        UUID id,
        @NotBlank(message = "National identity should not be blank")
        @Pattern(regexp = "\\d{11}", message = "Length of national identity must be 11 characters")
        String userNationalId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @Range(min = 1, max = 5, message = "Rate should be between {min} and {max}")
        Double rate,
        RatingReason reason) {

}
