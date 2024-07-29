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
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        List<ExchangeDto> exchangeDtos = new ArrayList<>();

        exchangeRepository.findAll()
                .forEach(exchange -> exchangeDtos.add(exchangeMapper.exchangeToDto(exchange)));

        return exchangeDtos;
    }

    @Override
    public Optional<ExchangeDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Optional<Exchange> maybeExchange = exchangeRepository.findById(id);

        return maybeExchange.map(exchangeMapper::exchangeToDto);
    }

    @Override
    public ExchangeDto createEntity(ExchangeDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Exchange exchange = exchangeMapper.dtoToExchange(request);
        Exchange savedExchange = exchangeRepository.save(exchange);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.EXCHANGE.getValue(), savedExchange.getId());

        return exchangeMapper.exchangeToDto(savedExchange);
    }

    @Override
    public ExchangeDto updateEntity(Integer id, ExchangeDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Exchange exchange = exchangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.EXCHANGE.getValue())));

        log.info(LogMessages.RESOURCE_FOUND, Entity.EXCHANGE.getValue());

        exchange.setFromCurrency(request.getFromCurrency());
        exchange.setToCurrency(request.getToCurrency());
        exchange.setRate(request.getRate());

        return exchangeMapper.exchangeToDto(exchangeRepository.save(exchange));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        if (!exchangeRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.EXCHANGE.getValue()));
        }

        log.info(LogMessages.RESOURCE_FOUND, Entity.EXCHANGE.getValue());

        exchangeRepository.deleteById(id);
    }


    public String exchangeMoney(Currency fromCurrency, Currency toCurrency, Double amount) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

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
