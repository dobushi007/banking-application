package com.ercanbeyen.bankingapplication.option;

import com.ercanbeyen.bankingapplication.constant.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TransactionFilteringOptions(
        TransactionType type,
        Integer senderAccountId,
        Integer receiverAccountId,
        @NotNull(message = "Minimum amount should not be null")
        @Min(value = 0, message = "Minimum amount value should be at least {value}")
        Double minimumAmount,
        LocalDate createAt) {
}
