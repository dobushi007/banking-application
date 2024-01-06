package com.ercanbeyen.bankingapplication.entity;

import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.City;
import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "accounts")
public non-sealed class Account extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "customer_national_id", referencedColumnName = "national_id")
    private Customer customer;
    @Enumerated(EnumType.STRING)
    private City branchLocation;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private Double balance;
    @Enumerated(EnumType.STRING)
    private AccountType type;
    /* Deposit Account fields */
    private Double interest;
    private Integer depositPeriod;
}
