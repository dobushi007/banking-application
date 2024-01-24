package com.ercanbeyen.bankingapplication.dto.request;

import com.ercanbeyen.bankingapplication.constant.enums.TransactionType;
import com.ercanbeyen.bankingapplication.entity.Account;

public record TransactionRequest(
        TransactionType transactionType,
        Account senderAccount,
        Account receiverAccount,
        Double amount,
        String explanation) {
}
