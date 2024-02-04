package com.ercanbeyen.bankingapplication.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Entity {
    CUSTOMER("Customer"),
    ACCOUNT("Account"),
    FILE("File"),
    NOTIFICATION("Notification"),
    TRANSACTION("Transaction"),
    REGULAR_TRANSFER_ORDER("RegularTransferOrder");

    private final String value;

    public String getCollectionUrl() {
        return "http://localhost:8080/api/v1/" + value.toLowerCase() + "s";
    }
}
