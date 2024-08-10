package com.ercanbeyen.bankingapplication.option;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ExchangeFilteringOptions extends BaseFilteringOptions {
    private Currency targetCurrency;
    private Currency sourceCurrency;
    private Double rate;
}
