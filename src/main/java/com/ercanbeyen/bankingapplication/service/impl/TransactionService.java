package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.AccountActivityType;
import com.ercanbeyen.bankingapplication.constant.enums.BalanceActivity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.request.AccountActivityRequest;
import com.ercanbeyen.bankingapplication.dto.request.TransferRequest;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.repository.AccountRepository;
import com.ercanbeyen.bankingapplication.service.AccountActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
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
        Pair<BalanceActivity, Account[]> activityParameters = constructActivityParameters(activityType, account);

        int numberOfUpdatedEntities = accountRepository.updateBalanceById(account.getId(), activityParameters.getValue0().name(), amount);
        log.info(LogMessages.NUMBER_OF_UPDATED_ENTITIES, numberOfUpdatedEntities);

        createAccountActivity(activityType, amount, explanation, activityParameters.getValue1());
    }

    public void transferMoneyBetweenAccounts(TransferRequest request, Integer senderAccountId, Double amount, Integer receiverAccountId, Account senderAccount, Account receiverAccount) {
        int numberOfUpdatedEntities = accountRepository.updateBalanceById(senderAccountId, BalanceActivity.DECREASE.name(), amount);
        log.info(LogMessages.NUMBER_OF_UPDATED_ENTITIES, numberOfUpdatedEntities);

        numberOfUpdatedEntities = accountRepository.updateBalanceById(receiverAccountId, BalanceActivity.INCREASE.name(), amount);
        log.info(LogMessages.NUMBER_OF_UPDATED_ENTITIES, numberOfUpdatedEntities);

        Account[] accounts = {senderAccount, receiverAccount};

        createAccountActivity(AccountActivityType.MONEY_TRANSFER, request.amount(), request.explanation(), accounts);
    }

    private void createAccountActivity(AccountActivityType activityType, Double amount, String explanation, Account[] accounts) {
        AccountActivityRequest accountActivityRequest = new AccountActivityRequest(activityType, accounts[0], accounts[1], amount, explanation);
        accountActivityService.createAccountActivity(accountActivityRequest);
    }

    /***
     *
     * @param activityType is for determining account activity type
     * @param account represents the responsible account
     * @return balance activity and position of the account (is account sender or receiver?)
     */
    private static Pair<BalanceActivity, Account[]> constructActivityParameters(AccountActivityType activityType, Account account) {
        Account[] accounts = new Account[2]; // first account is sender, second account is receiver
        BalanceActivity balanceActivity;

        switch (activityType) {
            case AccountActivityType.MONEY_DEPOSIT, AccountActivityType.FEE -> {
                accounts[1] = account; // receiver
                balanceActivity = BalanceActivity.INCREASE;
            }
            case AccountActivityType.WITHDRAWAL, AccountActivityType.CHARGE -> {
                accounts[0] = account; // sender
                balanceActivity = BalanceActivity.DECREASE;
            }
            default -> throw new ResourceConflictException(ResponseMessages.IMPROPER_ACCOUNT_ACTIVITY);
        }

        return new Pair<>(balanceActivity, accounts);
    }
}
