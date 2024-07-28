package com.ercanbeyen.bankingapplication.option;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public sealed class BaseFilteringOptions permits AccountFilteringOptions, CustomerFilteringOptions, ExchangeFilteringOptions, RegularTransferOrderOptions {
    private LocalDateTime createTime;
}
