package com.ercanbeyen.bankingapplication.dto.request;

import jakarta.validation.constraints.Min;

public record MoneyTransferRequest(Integer senderId, Integer receiverId, @Min(value = 1, message = "Minimum amount should be {value}") Double amount) {

}
