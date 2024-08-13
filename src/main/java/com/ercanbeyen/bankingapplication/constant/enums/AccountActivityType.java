package com.ercanbeyen.bankingapplication.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountActivityType {
    MONEY_DEPOSIT("Money deposit"),
    WITHDRAWAL("Withdrawal"),
    MONEY_TRANSFER("Money transfer"),
    MONEY_EXCHANGE("Money exchange"),
    FEE("Fee"),
    CHARGE("Charge");

    private final String value;

}
