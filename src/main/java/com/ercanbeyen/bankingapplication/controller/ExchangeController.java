package com.ercanbeyen.bankingapplication.controller;

import com.ercanbeyen.bankingapplication.dto.ExchangeDto;
import com.ercanbeyen.bankingapplication.option.ExchangeFilteringOptions;
import com.ercanbeyen.bankingapplication.service.impl.ExchangeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exchanges")
public class ExchangeController extends BaseController<ExchangeDto, ExchangeFilteringOptions> {
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        super(exchangeService);
        this.exchangeService = exchangeService;
    }
}
