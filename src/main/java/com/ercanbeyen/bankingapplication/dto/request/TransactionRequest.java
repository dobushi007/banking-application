package com.ercanbeyen.bankingapplication.dto.request;

import com.ercanbeyen.bankingapplication.constant.enums.TransactionType;

public record TransactionRequest(
        TransactionType transactionType,
        Integer senderAccountId,
        Integer receiverAccountId,
        Double amount,
        String explanation) {
}
