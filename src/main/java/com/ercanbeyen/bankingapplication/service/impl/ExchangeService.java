package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.ExchangeDto;
import com.ercanbeyen.bankingapplication.entity.Exchange;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.ExchangeMapper;
import com.ercanbeyen.bankingapplication.option.ExchangeFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.ExchangeRepository;
import com.ercanbeyen.bankingapplication.service.BaseService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService implements BaseService<ExchangeDto, ExchangeFilteringOptions> {
    private final ExchangeRepository exchangeRepository;
    private final ExchangeMapper exchangeMapper;

    @Override
    public List<ExchangeDto> getEntities(ExchangeFilteringOptions options) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        List<ExchangeDto> exchangeDtos = new ArrayList<>();
        exchangeRepository.findAll()
                .forEach(exchange -> exchangeDtos.add(exchangeMapper.entityToDto(exchange)));

        return exchangeDtos;
    }

    @Override
    public Optional<ExchangeDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());
        return exchangeRepository.findById(id)
                .map(exchangeMapper::entityToDto);
    }

    @Override
    public ExchangeDto createEntity(ExchangeDto request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Exchange exchange = exchangeMapper.dtoToEntity(request);
        Exchange savedExchange = exchangeRepository.save(exchange);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.EXCHANGE.getValue(), savedExchange.getId());

        return exchangeMapper.entityToDto(savedExchange);
    }

    @Override
    public ExchangeDto updateEntity(Integer id, ExchangeDto request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.EXCHANGE.getValue())));

        log.info(LogMessages.RESOURCE_FOUND, Entity.EXCHANGE.getValue());

        exchange.setFromCurrency(request.getFromCurrency());
        exchange.setToCurrency(request.getToCurrency());
        exchange.setRate(request.getRate());

        return exchangeMapper.entityToDto(exchangeRepository.save(exchange));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        if (!exchangeRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.EXCHANGE.getValue()));
        }

        log.info(LogMessages.RESOURCE_FOUND, Entity.EXCHANGE.getValue());

        exchangeRepository.deleteById(id);
    }


    public String exchangeMoney(Currency fromCurrency, Currency toCurrency, Double amount) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        double rate;
        Optional<Exchange> maybeExchange = exchangeRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
        log.info("Exchange is from {} to {}", fromCurrency, toCurrency);

        if (maybeExchange.isPresent()) {
            log.info("Exchange is present");
            rate = maybeExchange.get().getRate();
        } else {
            Exchange reverseExchange = exchangeRepository.findByFromCurrencyAndToCurrency(toCurrency, fromCurrency)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.EXCHANGE.getValue())));
            log.info("Reverse exchange is present");
            rate = Math.pow(reverseExchange.getRate(), -1);
        }

        double exchangedAmount = amount * rate;

        return amount + " " + fromCurrency.name() + " is successfully exchanged to " + exchangedAmount + " " + toCurrency.name() + " with rate " + rate;
    }

}
