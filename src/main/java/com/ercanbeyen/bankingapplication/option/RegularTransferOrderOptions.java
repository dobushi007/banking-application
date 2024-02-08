package com.ercanbeyen.bankingapplication.option;

import lombok.Data;

@Data
public non-sealed class RegularTransferOrderOptions extends BaseFilteringOptions {
    private Integer senderAccountId;
    private Integer receiverAccountId;
    private Integer period;
}
