package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.AccountActivityType;
import com.ercanbeyen.bankingapplication.constant.enums.BalanceActivity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.request.AccountActivityRequest;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.repository.AccountRepository;
import com.ercanbeyen.bankingapplication.service.AccountActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    private final AccountRepository accountRepository;
    private final AccountActivityService accountActivityService;

    public void updateBalanceOfSingleAccount(AccountActivityType activityType, Double amount, Account account, String explanation) {
        Account[] accounts = new Account[2]; // first account is sender, second account is receiver
        BalanceActivity balanceActivity;

        switch (activityType) {
            case MONEY_DEPOSIT, FEE -> {
                accounts[1] = account;
                balanceActivity = BalanceActivity.INCREASE;
            }
            case WITHDRAWAL, CHARGE -> {
                accounts[0] = account;
                balanceActivity = BalanceActivity.DECREASE;
            }
            default -> throw new ResourceConflictException(ResponseMessages.IMPROPER_ACCOUNT_ACTIVITY);
        }

        int numberOfUpdatedEntities = accountRepository.updateBalanceById(account.getId(), balanceActivity.name(), amount);
        log.info(LogMessages.NUMBER_OF_UPDATED_ENTITIES, numberOfUpdatedEntities);

        createAccountActivity(activityType, amount, explanation, accounts);
    }

    private void createAccountActivity(AccountActivityType activityType, Double amount, String explanation, Account[] accounts) {
        AccountActivityRequest accountActivityRequest = new AccountActivityRequest(activityType, accounts[0], accounts[1], amount, explanation);
        accountActivityService.createAccountActivity(accountActivityRequest);
    }
}
