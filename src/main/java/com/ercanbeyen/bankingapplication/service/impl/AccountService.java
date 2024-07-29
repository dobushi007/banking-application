package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.*;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.dto.request.TransactionRequest;
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
import com.ercanbeyen.bankingapplication.service.TransactionService;
import com.ercanbeyen.bankingapplication.util.AccountUtils;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final TransactionService transactionService;

    @Override
    public List<AccountDto> getEntities(AccountFilteringOptions options) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Predicate<Account> accountPredicate = account -> (options.getType() == null || options.getType() == account.getType())
                && (options.getCreateTime() == null || options.getCreateTime().toLocalDate().isEqual(options.getCreateTime().toLocalDate()));
        List<AccountDto> accountDtos = new ArrayList<>();

        accountRepository.findAll()
                .stream()
                .filter(accountPredicate)
                .forEach(account -> accountDtos.add(accountMapper.accountToDto(account)));

        return accountDtos;
    }

    @Override
    public Optional<AccountDto> getEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Optional<Account> accountOptional = accountRepository.findById(id);

        return accountOptional.map(accountMapper::accountToDto);
    }

    @Override
    public AccountDto createEntity(AccountDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Account account = accountMapper.dtoToAccount(request);

        Customer customer = customerService.findCustomerByNationalId(request.getCustomerNationalId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        account.setCustomer(customer);

        Account savedAccount = accountRepository.save(account);

        return accountMapper.accountToDto(savedAccount);
    }

    @Override
    public AccountDto updateEntity(Integer id, AccountDto request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Account account = findAccountById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        account.setCity(request.getCity());

        return accountMapper.accountToDto(accountRepository.save(account));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        if (!doesAccountExist(id)) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.ACCOUNT.getValue()));
        }

        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());
        accountRepository.deleteById(id);
    }

    public String applyUnidirectionalAccountOperation(Integer id, AccountOperation operation, Double amount) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Account account = findAccountById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        TransactionRequest transactionRequest;
        String result;

        switch (operation) {
            case AccountOperation.ADD -> {
                result = addMoney(account, amount);
                transactionRequest = new TransactionRequest(TransactionType.ADD_MONEY, null, account, amount, null);
            }
            case AccountOperation.WITHDRAW -> {
                result = withdrawMoney(account, amount);
                transactionRequest = new TransactionRequest(TransactionType.WITHDRAW_MONEY, account, null, amount, null);
            }
            default -> throw new ResourceExpectationFailedException("Unknown account operation");
        }

        transactionService.createTransaction(transactionRequest);

        return result;
    }

    public String addMoneyToDepositAccount(Integer id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Account account = findAccountById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        if (!AccountUtils.checkAccountForPeriodicMoneyAdd(account.getType(), account.getUpdatedAt(), account.getDepositPeriod())) {
            log.warn("Deposit period is not completed");
            return "Today is not the completion of deposit period";
        }

        Double amount = AccountUtils.calculateInterest(account.getBalance(), account.getInterestRatio());

        return addMoney(account, amount);
    }

    @Transactional
    public String transferMoney(TransferRequest request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

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

        TransactionRequest transactionRequest = new TransactionRequest(
                TransactionType.MONEY_TRANSFER,
                senderAccount,
                receiverAccount,
                amount,
                request.explanation()
        );

        transactionService.createTransaction(transactionRequest);

        return message;
    }

    public Account findAccount(Integer id) {
        return findAccountById(id);
    }

    public String getTotalAccounts(City city, AccountType type, Currency currency) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        int count = accountRepository.getTotalAccountsByCityAndTypeAndCurrency(
                city.name(),
                type.name(),
                currency.name()
        );
        log.info("Total count: {}", count);
        return String.format("Total %s accounts in %s currency in %s is %d", type, currency, city, count);
    }

    public List<CustomerStatisticsResponse> getCustomersHaveMaximumBalance(AccountType type, Currency currency, City city) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        if (Optional.ofNullable(city).isPresent()) {
            return accountRepository.getCustomersHaveMaximumBalanceByTypeAndCurrencyAndCity(type, currency, city);
        } else {
            return accountRepository.getCustomersHaveMaximumBalanceByTypeAndCurrency(type, currency);
        }
    }

    @Transactional
    private String addMoney(Account account, Double amount) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Double previousBalance = account.getBalance();
        Double nextBalance = previousBalance + amount;
        log.info(LogMessages.BALANCE_UPDATE, previousBalance, nextBalance);

        account.setBalance(nextBalance);
        accountRepository.save(account);

        return AccountUtils.constructResponseMessageForUnidirectionalAccountOperations(AccountOperation.ADD, amount, account.getId(), account.getCurrency());
    }

    @Transactional
    private String withdrawMoney(Account account, Double amount) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        AccountUtils.checkBalance(account.getBalance(), amount);

        Double previousBalance = account.getBalance();
        Double nextBalance = previousBalance - amount;
        log.info(LogMessages.BALANCE_UPDATE, previousBalance, nextBalance);

        account.setBalance(nextBalance);
        accountRepository.save(account);

        return AccountUtils.constructResponseMessageForUnidirectionalAccountOperations(AccountOperation.WITHDRAW, amount, account.getId(), account.getCurrency());
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
