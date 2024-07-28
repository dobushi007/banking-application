package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "exchanges")
public final class Exchange extends BaseEntity {
    private Currency fromCurrency;
    private Currency toCurrency;
    private Double rate;
}
