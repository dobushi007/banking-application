package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExchangeUtils {
    public void checkCurrencies(Currency from, Currency to) {
        if (from == to) {
            throw new ResourceConflictException(String.format(ResponseMessages.UNPAIRED_CURRENCIES, "different"));
        }
    }
}
