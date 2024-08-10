package com.ercanbeyen.bankingapplication.dto.request;

import com.ercanbeyen.bankingapplication.constant.enums.AccountActivityType;
import com.ercanbeyen.bankingapplication.entity.Account;

public record AccountActivityRequest(
        AccountActivityType activityType,
        Account senderAccount,
        Account receiverAccount,
        Double amount,
        String explanation) {
}
