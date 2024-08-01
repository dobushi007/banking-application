package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.*;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.dto.request.AccountActivityRequest;
import com.ercanbeyen.bankingapplication.dto.request.TransferRequest;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.AccountMapper;
import com.ercanbeyen.bankingapplication.option.AccountFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.AccountRepository;
import com.ercanbeyen.bankingapplication.dto.response.CustomerStatisticsResponse;
import com.ercanbeyen.bankingapplication.service.BaseService;
import com.ercanbeyen.bankingapplication.service.AccountActivityService;
import com.ercanbeyen.bankingapplication.util.AccountUtils;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements BaseService<AccountDto, AccountFilteringOptions> {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CustomerService customerService;
    private final AccountActivityService accountActivityService;

    @Override
    public List<AccountDto> getEntities(AccountFilteringOptions options) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Predicate<Account> accountPredicate = account -> (options.getType() == null || options.getType() == account.getType())
                && (options.getCreateTime() == null || options.getCreateTime().toLocalDate().isEqual(options.getCreateTime().toLocalDate()));
        List<AccountDto> accountDtos = new ArrayList<>();

        accountRepository.findAll()
                .stream()
                .filter(accountPredicate)
                .forEach(account -> accountDtos.add(accountMapper.entityToDto(account)));

        return accountDtos;
    }

    @Override
    public Optional<AccountDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());
        return accountRepository.findById(id)
                .map(accountMapper::entityToDto);
    }

    @Override
    public AccountDto createEntity(AccountDto request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account account = accountMapper.dtoToEntity(request);

        Customer customer = customerService.findByNationalId(request.getCustomerNationalId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        account.setCustomer(customer);
        Account savedAccount = accountRepository.save(account);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.ACCOUNT.getValue(), savedAccount.getId());

        return accountMapper.entityToDto(savedAccount);
    }

    @Override
    public AccountDto updateEntity(Integer id, AccountDto request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account account = findAccountById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        account.setCity(request.getCity());

        return accountMapper.entityToDto(accountRepository.save(account));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        if (!doesAccountExist(id)) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.ACCOUNT.getValue()));
        }

        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());
        accountRepository.deleteById(id);
    }

    @Transactional
    public String applyUnidirectionalAccountOperation(Integer id, AccountActivityType activityType, Double amount) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account account = findAccountById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        AccountActivityRequest accountActivityRequest;

        switch (activityType) {
            case AccountActivityType.MONEY_DEPOSIT -> accountActivityRequest = new AccountActivityRequest(AccountActivityType.MONEY_DEPOSIT, null, account, amount, null);
            case AccountActivityType.WITHDRAWAL -> accountActivityRequest = new AccountActivityRequest(AccountActivityType.WITHDRAWAL, account, null, amount, null);
            default -> throw new ResourceExpectationFailedException("Unknown account activity type");
        }

        int numberOfUpdatedEntities = accountRepository.updateBalance(id, activityType.name(), amount);
        log.info(LogMessages.NUMBER_OF_UPDATED_ENTITIES, numberOfUpdatedEntities);

        accountActivityService.createAccountActivity(accountActivityRequest);

        return AccountUtils.constructResponseMessageForUnidirectionalAccountOperations(activityType, amount, account.getId(), account.getCurrency());
    }

    @Transactional
    public String addMoneyToDepositAccount(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account account = findAccountById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        if (!AccountUtils.checkAccountForPeriodicMoneyAdd(account.getType(), account.getUpdatedAt(), account.getDepositPeriod())) {
            log.warn("Deposit period is not completed");
            return "Today is not the completion of deposit period";
        }

        Double amount = AccountUtils.calculateInterest(account.getBalance(), account.getInterestRatio());

        int numberOfUpdatedEntities = accountRepository.updateBalance(id, AccountActivityType.MONEY_DEPOSIT.name(), amount);
        log.info(LogMessages.NUMBER_OF_UPDATED_ENTITIES, numberOfUpdatedEntities);

        AccountActivityRequest request = new AccountActivityRequest(
                AccountActivityType.FEES_CHARGES,
                null,
                account,
                amount,
                "Fee is transferred, because deposit period is completed"
        );

        accountActivityService.createAccountActivity(request);

        return AccountUtils.constructResponseMessageForUnidirectionalAccountOperations(AccountActivityType.MONEY_DEPOSIT, amount, account.getId(), account.getCurrency());
    }

    @Transactional
    public String transferMoney(TransferRequest request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account senderAccount = findAccountById(request.senderAccountId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        Account receiverAccount = findAccountById(request.receiverAccountId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        Double amount = request.amount();

        checkAccountsBeforeMoneyTransfer(senderAccount, receiverAccount, amount);

        double newSenderBalance = senderAccount.getBalance() - request.amount();
        double newReceiverBalance = receiverAccount.getBalance() + request.amount();

        senderAccount.setBalance(newSenderBalance);
        receiverAccount.setBalance(newReceiverBalance);

        List<Account> accounts = List.of(senderAccount, receiverAccount);

        accountRepository.saveAll(accounts);

        String message = amount + " " + senderAccount.getCurrency() + " is successfully transferred from account "
                + senderAccount.getId() + " to account " + receiverAccount.getId();

        log.info(LogMessages.TRANSACTION_MESSAGE, message);

        AccountActivityRequest accountActivityRequest = new AccountActivityRequest(
                AccountActivityType.MONEY_TRANSFER,
                senderAccount,
                receiverAccount,
                amount,
                request.explanation()
        );

        accountActivityService.createAccountActivity(accountActivityRequest);

        return message;
    }

    public Account findAccount(Integer id) {
        return findAccountById(id);
    }

    public String getTotalAccounts(City city, AccountType type, Currency currency) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        int count = accountRepository.getTotalAccountsByCityAndTypeAndCurrency(
                city.name(),
                type.name(),
                currency.name()
        );
        log.info("Total count: {}", count);
        return String.format("Total %s accounts in %s currency in %s is %d", type, currency, city, count);
    }

    public List<CustomerStatisticsResponse> getCustomersHaveMaximumBalance(AccountType type, Currency currency, City city) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        if (Optional.ofNullable(city).isPresent()) {
            return accountRepository.getCustomersHaveMaximumBalanceByTypeAndCurrencyAndCity(type, currency, city);
        } else {
            return accountRepository.getCustomersHaveMaximumBalanceByTypeAndCurrency(type, currency);
        }
    }

    private boolean doesAccountExist(Integer id) {
        return accountRepository.existsById(id);
    }

    private Account findAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.ACCOUNT.getValue())));
    }

    private static void checkAccountsBeforeMoneyTransfer(Account senderAccount, Account receiverAccount, Double amount) {
        if (senderAccount.getCurrency() != receiverAccount.getCurrency()) {
            throw new ResourceConflictException("Currencies of the accounts must be same");
        }

        AccountUtils.checkBalance(senderAccount.getBalance(), amount);
    }
}
