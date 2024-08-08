package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.entity.Exchange;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRepository extends BaseRepository<Exchange> {
    boolean existsByBaseCurrencyAndTargetCurrency(Currency baseCurrency, Currency targetCurrency);
    boolean existsByTargetCurrencyAndBaseCurrency(Currency targetCurrency, Currency baseCurrency);
}
