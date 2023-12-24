package com.ercanbeyen.bankingapplication.dto;

import com.ercanbeyen.bankingapplication.constant.enums.City;
import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import lombok.Data;

@Data
public non-sealed class AccountDto extends BaseDto {
    private Integer customerId;
    private City city;
    private Currency currency;
    private Double balance;
}
