package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import lombok.Data;

@Data
public final class ExchangeDto extends BaseDto {
    private Currency fromCurrency;
    private Currency toCurrency;
    private Double rate;
}
