package com.ercanbeyen.bankingapplication.view;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;


@Getter
@Entity(name = "exchange_views")
@Immutable
@Subselect("""
        SELECT e.id, e.target_currency, e.base_currency, e.rate * ((100 + e.sell_percentage) / 100) AS sell_rate, e.rate * ((100 - e.buy_percentage) / 100) AS buy_rate
        FROM exchanges e
        """)
@Synchronize("exchanges")
public class ExchangeView {
    @Id
    private Integer id;
    @Column(name = "target_currency")
    private Currency targetCurrency;
    @Column(name = "base_currency")
    private Currency baseCurrency;
    @Column(name = "buy_rate")
    private Double buyRate;  // bank buy rate
    @Column(name = "sell_rate")
    private Double sellRate; // bank sell rate
}
