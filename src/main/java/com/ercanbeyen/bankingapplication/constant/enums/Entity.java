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
        String resource = (this == REGULAR_TRANSFER_ORDER) ? "regular-transfer-order" : value.toLowerCase();
        return "http://localhost:8080/api/v1/" + resource + "s";
    }
}
