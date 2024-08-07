package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "exchanges")
public final class Exchange extends BaseEntity {
    private Currency targetCurrency;
    private Currency baseCurrency;
    private Double rate;
    private Double buyPercentage;  // bank buy percentage
    private Double sellPercentage; // bank sell percentage
}
