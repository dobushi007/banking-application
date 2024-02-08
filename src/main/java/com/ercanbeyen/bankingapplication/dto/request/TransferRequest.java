package com.ercanbeyen.bankingapplication.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransferRequest(
        @NotNull(message = "Sender account id should not be null")
        Integer senderAccountId,
        @NotNull(message = "Receiver account id should not be null")
        Integer receiverAccountId,
        @Min(value = 1, message = "Minimum amount is {value}")
        Double amount,
        @Size(max = 500, message = "Maximum length for explanation is {max} characters")
        String explanation) {

}
