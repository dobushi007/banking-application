package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.AccountOperation;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.dto.request.TransferRequest;
import com.ercanbeyen.bankingapplication.entity.Account;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
public class AccountUtils {
    private AccountUtils() {}

    public static void checkAccountConstruction(AccountDto accountDto) {
        checkAccountType(accountDto);
        checkDepositPeriod(accountDto);
    }

    public static void checkMoneyTransferRequest(TransferRequest request) {
        if (Objects.equals(request.senderAccountId(), request.receiverAccountId())) {
            throw new ResourceExpectationFailedException("Identity of sender and receiver accounts should not be equal");
        }
    }

    public static void checkBalance(Account account, Double amount) {
        if (account.getBalance() < amount) {
            throw new ResourceExpectationFailedException("Insufficient funds");
        }
    }

    public static void checkCurrenciesForMoneyTransfer(Account senderAccount, Account receiverAccount) {
        if (senderAccount.getCurrency() != receiverAccount.getCurrency()) {
            throw new ResourceConflictException("Currencies of the accounts must be same");
        }
    }

    public static double calculateInterestAmountForDeposit(Double balance, Double interest) {
        return (balance == 0 || interest == 0) ? 0 : ((interest * 100) / balance);
    }

    public static String constructResponseMessageForUnidirectionalAccountOperations(AccountOperation operation, Double amount, Account account) {
        String messageTemplate = amount + " " + account.getCurrency() + " is successfully %s account " + account.getId();

        return switch (operation) {
            case ADD -> String.format(messageTemplate, "added to");
            case WITHDRAW -> String.format(messageTemplate, "withdrawn from");
        };
    }

    public static boolean checkDepositAccountForPeriodicMoneyAdd(Account account) {
        AccountType accountType = account.getType();
        log.info("Account Type: {}", accountType);

        if (accountType != AccountType.DEPOSIT) {
            throw new ResourceConflictException("This money add operation is for deposit accounts");
        }

        LocalDate today = LocalDate.now();
        LocalDate updateDate = account.getUpdatedAt()
                .toLocalDate()
                .plusMonths(account.getDepositPeriod());

        return updateDate.isEqual(today);
    }

    private static void checkAccountType(AccountDto accountDto) {
        boolean isInterestNull = isNull.test(accountDto.getInterest());
        boolean isDepositPeriodNull = isNull.test(accountDto.getDepositPeriod());

        AccountType accountType = accountDto.getType();
        String message = "have interest and deposit period values";

        if ((accountType == AccountType.DEPOSIT) && (isInterestNull || isDepositPeriodNull)) {
            String exceptionMessage = accountType + " must " + message;
            throw new ResourceExpectationFailedException(exceptionMessage);
        } else if ((accountType == AccountType.CHECKING) && (!isInterestNull || !isDepositPeriodNull)) {
            String exceptionMessage = accountType + " does not " + message;
            throw new ResourceExpectationFailedException(exceptionMessage);
        }
    }

    private static void checkDepositPeriod(AccountDto accountDto) {
      if (accountDto.getType() == AccountType.CHECKING) {
          log.warn("Checking Account does not have deposit period");
          return;
      }

      List<Integer> depositPeriodArray = List.of(1, 3, 6, 12);

      if (!depositPeriodArray.contains(accountDto.getDepositPeriod())) {
          throw new ResourceExpectationFailedException("Deposit period is invalid. Valid values are " + depositPeriodArray);
      }
    }

    private static final Predicate<Object> isNull = Objects::isNull;
}
