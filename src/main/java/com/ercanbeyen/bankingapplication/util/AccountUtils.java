package com.ercanbeyen.bankingapplication.util;

import com.ercanbeyen.bankingapplication.constant.enums.AccountOperation;
import com.ercanbeyen.bankingapplication.constant.enums.AccountType;
import com.ercanbeyen.bankingapplication.constant.enums.Currency;
import com.ercanbeyen.bankingapplication.dto.AccountDto;
import com.ercanbeyen.bankingapplication.dto.request.TransferRequest;
import com.ercanbeyen.bankingapplication.exception.ResourceConflictException;
import com.ercanbeyen.bankingapplication.exception.ResourceExpectationFailedException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
@UtilityClass
public final class AccountUtils {
    private static final List<Integer> DEPOSIT_PERIODS = List.of(1, 3, 6, 12);
    private static final Double MAXIMUM_TRANSFER_LIMIT = 1_000_000D;

    public static void checkAccountConstruction(AccountDto accountDto) {
        checkAccountType(accountDto);
        checkDepositPeriod(accountDto);
    }

    public static void checkMoneyTransferRequest(TransferRequest request) {
        if (Objects.equals(request.senderAccountId(), request.receiverAccountId())) {
            throw new ResourceExpectationFailedException("Identity of sender and receiver accounts should not be equal");
        }

        if (request.amount() >= MAXIMUM_TRANSFER_LIMIT) {
            throw new ResourceExpectationFailedException("Maximum transfer limit (" + MAXIMUM_TRANSFER_LIMIT + ") is exceeded");
        }
    }

    public static void checkBalance(Double balance, Double threshold) {
        if (balance < threshold) {
            throw new ResourceExpectationFailedException("Insufficient funds");
        }
    }

    public static double calculateInterest(Double balance, Double interestRatio) {
        checkValidityOfBalanceAndInterestRatio(balance, interestRatio);
        return (balance == 0 || interestRatio == 0) ? 0 : ((interestRatio * 100) / balance);
    }

    public static boolean checkAccountForPeriodicMoneyAdd(AccountType accountType, LocalDateTime updatedAt, Integer depositPeriod) {
        checkAccountTypeAndDepositPeriodForPeriodMoneyAdd(accountType, depositPeriod);

        LocalDate isGoingToBeUpdatedAt = updatedAt
                .toLocalDate()
                .plusMonths(depositPeriod);

        return isGoingToBeUpdatedAt.isEqual(LocalDate.now());
    }

    public static String constructResponseMessageForUnidirectionalAccountOperations(AccountOperation operation, Double amount, Integer id, Currency currency) {
        String messageTemplate = amount + " " + currency + " is successfully %s account " + id;

        return switch (operation) {
            case ADD -> String.format(messageTemplate, "added to");
            case WITHDRAW -> String.format(messageTemplate, "withdrawn from");
        };
    }

    private static void checkAccountTypeAndDepositPeriodForPeriodMoneyAdd(AccountType accountType, Integer depositPeriod) {
        if (accountType != AccountType.DEPOSIT) {
            throw new ResourceConflictException("This money add operation is for deposit accounts");
        }

        checkValidityOfDepositPeriod(depositPeriod);
    }

    private static void checkValidityOfBalanceAndInterestRatio(Double balance, Double interestRatio) {
        boolean isBalanceValid = balance < 0;
        boolean isInterestRatioValid = interestRatio < 0;

        if (isBalanceValid && isInterestRatioValid) {
            throw new ResourceConflictException("Invalid balance and interest ratio");
        }

        if (isInterestRatioValid) {
            throw new ResourceConflictException("Invalid interest ratio");
        }

        if (isBalanceValid) {
            throw new ResourceConflictException("Invalid balance");
        }
    }

    private static void checkAccountType(AccountDto accountDto) {
        boolean isInterestNull = isNull.test(accountDto.getInterestRatio());
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

      checkValidityOfDepositPeriod(accountDto.getDepositPeriod());
    }

    private static void checkValidityOfDepositPeriod(Integer depositPeriod) {
        if (!DEPOSIT_PERIODS.contains(depositPeriod)) {
            throw new ResourceExpectationFailedException("Deposit period is invalid. Valid values are " + DEPOSIT_PERIODS);
        }
    }

    private static final Predicate<Object> isNull = Objects::isNull;
}
