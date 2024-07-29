package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.entity.Exchange;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRepository extends BaseRepository<Exchange> {
    Optional<Exchange> findByFromCurrencyAndToCurrency(Currency fromCurrency, Currency toCurrency);
}
