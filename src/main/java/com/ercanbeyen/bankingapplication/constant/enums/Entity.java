package com.ercanbeyen.bankingapplication.constant.enums;

import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Entity {
    CUSTOMER("Customer"),
    ACCOUNT("Account"),
    FILE("File"),
    NOTIFICATION("Notification"),
    ACCOUNT_ACTIVITY("AccountActivity"),
    REGULAR_TRANSFER_ORDER("RegularTransferOrder"),
    RATING("Rating"),
    EXCHANGE("Exchange"),
    GENERAL("Entity");

    private final String value;

    public String getCollectionUrl() {
        if (this == GENERAL) {
            throw new ResourceExpectationFailedException(ResponseMessages.INVALID_CONTENT_TYPE);
        }

        String resource = getResource();
        return "http://localhost:8080/api/v1/" + resource + "s";
    }

    private String getResource() {
        return switch (this) {
            case REGULAR_TRANSFER_ORDER -> "regular-transfer-order";
            case ACCOUNT_ACTIVITY -> "account-activitie";
            default -> value.toLowerCase();
        };
    }
}
