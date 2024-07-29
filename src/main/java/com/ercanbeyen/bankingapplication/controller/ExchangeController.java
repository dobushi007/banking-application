package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.dto.ExchangeDto;
import com.ercanbeyen.bankingapplication.option.ExchangeFilteringOptions;
import com.ercanbeyen.bankingapplication.service.impl.ExchangeService;
import com.ercanbeyen.bankingapplication.util.ExchangeUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchanges")
public class ExchangeController extends BaseController<ExchangeDto, ExchangeFilteringOptions> {
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        super(exchangeService);
        this.exchangeService = exchangeService;
    }

    @GetMapping("/{from}/{to}/{amount}")
    public ResponseEntity<String> exchangeMoney(
            @PathVariable("from") Currency fromCurrency,
            @PathVariable("to") Currency toCurrency,
            @PathVariable("amount") @Valid @Min(value = 1, message = "Minimum amount should be {value}") Double amount) {
        ExchangeUtils.checkCurrencies(fromCurrency, toCurrency);
        return ResponseEntity.ok(exchangeService.exchangeMoney(fromCurrency, toCurrency, amount));
    }
}
