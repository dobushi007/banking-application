package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.AccountActivityType;

import java.time.LocalDateTime;

public record AccountActivityDto(
        AccountActivityType type,
        Integer senderAccountId,
        Integer receiverAccountId,
        Double amount,
        LocalDateTime createdAt,
        String explanation) {

}
