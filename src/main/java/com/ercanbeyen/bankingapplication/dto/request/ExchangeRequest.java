package com.ercanbeyen.bankingapplication.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ExchangeRequest(
        @NotNull(message = "Seller account id should not be null") Integer sellerId,
        @NotNull(message = "Buyer account id should not be null") Integer buyerId,
        @Min(value = 1, message = "Minimum amount is {value}") Double amount) {

}
