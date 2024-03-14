package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.TransactionType;

import java.time.LocalDateTime;

public record TransactionDto(
        TransactionType type,
        Integer senderAccountId,
        Integer receiverAccountId,
        Double amount,
        LocalDateTime createdAt,
        String explanation) {

}
