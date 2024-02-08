package com.ercanbeyen.bankingapplication.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegularTransferDto(
        @NotNull(message = "receiver account id should not be null")
        Integer receiverAccountId,
        @NotNull(message = "Amount should not be null")
        @Min(value = 1, message = "Minimum amount should be {value}")
        Double amount,
        String explanation) {

}
