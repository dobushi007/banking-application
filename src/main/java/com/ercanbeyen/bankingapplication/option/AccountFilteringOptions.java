package com.ercanbeyen.bankingapplication.option;

import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public non-sealed class AccountFilteringOptions extends BaseFilteringOptions {
    private AccountType type;
    private Boolean isClosed;
}
