package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.*;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.dto.NotificationDto;
import com.ercanbeyen.bankingapplication.dto.request.AccountActivityRequest;
import com.ercanbeyen.bankingapplication.dto.request.ExchangeRequest;
import com.ercanbeyen.bankingapplication.dto.request.TransferRequest;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.entity.Customer;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.AccountMapper;
import com.ercanbeyen.bankingapplication.option.AccountFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.AccountRepository;
import com.ercanbeyen.bankingapplication.dto.response.CustomerStatisticsResponse;
import com.ercanbeyen.bankingapplication.service.BaseService;
import com.ercanbeyen.bankingapplication.service.AccountActivityService;
import com.ercanbeyen.bankingapplication.service.NotificationService;
import com.ercanbeyen.bankingapplication.util.AccountUtils;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final ExchangeService exchangeService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;

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
    public AccountDto getEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account account = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        return accountMapper.entityToDto(account);
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

        Account account = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        AccountUtils.checkCurrencies(account.getCurrency(), request.getCurrency());

        account.setCity(request.getCity());
        account.setInterestRatio(request.getInterestRatio());
        account.setDepositPeriod(request.getDepositPeriod());

        return accountMapper.entityToDto(accountRepository.save(account));
    }

    @Override
    public void deleteEntity(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.ACCOUNT.getValue()));
        }

        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        accountRepository.deleteById(id);
        log.info(LogMessages.RESOURCE_DELETE_SUCCESS, Entity.ACCOUNT.getValue(), id);
    }

    public String updateBalanceOfCurrentAccount(Integer id, AccountActivityType activityType, Double amount) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account account = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        transactionService.updateBalanceOfSingleAccount(activityType, amount, account, null);

        return AccountUtils.constructResponseMessageForUnidirectionalAccountOperations(activityType, amount, account.getId(), account.getCurrency());
    }

    public String updateBalanceOfDepositAccount(Integer id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account account = findById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        if (!AccountUtils.checkAccountForPeriodicMoneyAdd(account.getType(), account.getUpdatedAt(), account.getDepositPeriod())) {
            log.warn("Deposit period is not completed");
            return "Today is not the completion of deposit period";
        }

        Double amount = AccountUtils.calculateInterest(account.getBalance(), account.getInterestRatio());
        transactionService.updateBalanceOfSingleAccount(AccountActivityType.FEE, amount, account, "Fee is transferred, because deposit period is completed");

        NotificationDto notificationDto = new NotificationDto(account.getCustomer().getNationalId(), String.format("Term of your %s is deposit account has been renewed.", account.getCurrency()));
        notificationService.createNotification(notificationDto);

        return amount + " " + account.getCurrency() + " fee is successfully transferred to account " + account.getId();
    }

    @Transactional
    public String transferMoney(TransferRequest request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Integer senderAccountId = request.senderAccountId();
        Integer receiverAccountId = request.receiverAccountId();

        Account senderAccount = findById(senderAccountId);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        Account receiverAccount = findById(receiverAccountId);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        Double amount = request.amount();

        checkAccountsBeforeMoneyTransfer(senderAccount, receiverAccount, amount);

        accountRepository.updateBalanceById(senderAccountId, BalanceActivity.DECREASE.name(), amount);
        accountRepository.updateBalanceById(receiverAccountId, BalanceActivity.INCREASE.name(), amount);

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

    @Transactional
    public String exchangeMoney(ExchangeRequest request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(),LoggingUtils.getCurrentMethodName());

        Account buyerAccount = findById(request.buyerId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        Account sellerAccount = findById(request.sellerId());
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT.getValue());

        Double requestedAmount = request.amount();
        AccountUtils.checkBalance(sellerAccount.getBalance(), requestedAmount);

        Double exchangedAmount = exchangeService.exchangeMoney(sellerAccount, buyerAccount, requestedAmount);

        accountRepository.updateBalanceById(request.sellerId(), BalanceActivity.DECREASE.name(), requestedAmount);
        accountRepository.updateBalanceById(request.buyerId(), BalanceActivity.INCREASE.name(), exchangedAmount);

        String exchangeMessage = """
                Money exchange (from %s to %s) operation is completed.
                Customer National Id: %s
                From Account Id: %s
                To Account Id: %s
                Converted Amount: %s,
                Time: %s
                """;

        AccountActivityRequest accountActivityRequest = new AccountActivityRequest(
                AccountActivityType.MONEY_EXCHANGE,
                sellerAccount,
                buyerAccount,
                requestedAmount,
                String.format(
                        exchangeMessage,
                        sellerAccount.getCurrency(),
                        buyerAccount.getCurrency(),
                        buyerAccount.getCustomer().getNationalId(),
                        buyerAccount.getId(),
                        sellerAccount.getId(),
                        requestedAmount,
                        LocalDateTime.now()
                )
        );

        accountActivityService.createAccountActivity(accountActivityRequest);

        return  requestedAmount + " " + sellerAccount.getCurrency() + " is successfully exchanged to " + exchangedAmount + " " + buyerAccount.getCurrency();
    }

    public Account findById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.ACCOUNT.getValue())));
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

    private static void checkAccountsBeforeMoneyTransfer(Account senderAccount, Account receiverAccount, Double amount) {
        AccountUtils.checkCurrencies(senderAccount.getCurrency(), receiverAccount.getCurrency());
        AccountUtils.checkBalance(senderAccount.getBalance(), amount);
    }
}
