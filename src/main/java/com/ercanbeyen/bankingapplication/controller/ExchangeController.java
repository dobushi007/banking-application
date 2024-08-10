package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.dto.ExchangeDto;
import com.ercanbeyen.bankingapplication.dto.response.MessageResponse;
import com.ercanbeyen.bankingapplication.entity.ExchangeView;
import com.ercanbeyen.bankingapplication.option.ExchangeFilteringOptions;
import com.ercanbeyen.bankingapplication.service.impl.ExchangeService;
import com.ercanbeyen.bankingapplication.util.ExchangeUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchanges")
public class ExchangeController extends BaseController<ExchangeDto, ExchangeFilteringOptions> {
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        super(exchangeService);
        this.exchangeService = exchangeService;
    }

    @GetMapping("/views")
    public ResponseEntity<List<ExchangeView>> getExchangeViews() {
        return ResponseEntity.ok(exchangeService.getExchangeViews());
    }

    @GetMapping("/{from}/{to}/{amount}")
    public ResponseEntity<MessageResponse<String>> exchangeMoney(
            @PathVariable("from") Currency fromCurrency,
            @PathVariable("to") Currency toCurrency,
            @PathVariable("amount") @Valid @Min(value = 1, message = "Minimum amount should be {value}") Double amount) {
        ExchangeUtils.checkCurrencies(fromCurrency, toCurrency);
        MessageResponse<String> response = new MessageResponse<>(exchangeService.calculateMoneyExchange(fromCurrency, toCurrency, amount));
        return ResponseEntity.ok(response);
    }
}
