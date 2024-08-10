package com.ercanbeyen.bankingapplication.repository;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.entity.ExchangeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeViewRepository extends JpaRepository<ExchangeView, Integer> {
    Optional<ExchangeView> findByBaseCurrencyAndTargetCurrency(Currency baseCurrency, Currency targetCurrency);
    Optional<ExchangeView> findByTargetCurrencyAndBaseCurrency(Currency targetCurrency, Currency baseCurrency);
}
