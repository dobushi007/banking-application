package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.AccountActivityDto;
import com.ercanbeyen.bankingapplication.dto.request.AccountActivityRequest;
import com.ercanbeyen.bankingapplication.entity.AccountActivity;
import com.ercanbeyen.bankingapplication.view.AccountActivityView;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.AccountActivityMapper;
import com.ercanbeyen.bankingapplication.option.AccountActivityFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.AccountActivityRepository;
import com.ercanbeyen.bankingapplication.repository.AccountActivityViewRepository;
import com.ercanbeyen.bankingapplication.service.AccountActivityService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountActivityServiceImpl implements AccountActivityService {
    private final AccountActivityRepository accountActivityRepository;
    private final AccountActivityViewRepository accountActivityViewRepository;
    private final AccountActivityMapper accountActivityMapper;

    @Override
    public List<AccountActivityDto> getAccountActivities(AccountActivityFilteringOptions options) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        Predicate<AccountActivity> transactionPredicate = accountActivity -> (options.type() == null || options.type() == accountActivity.getType())
                && (options.senderAccountId() == null || options.senderAccountId().equals(accountActivity.getSenderAccount().getId()))
                && (options.receiverAccountId() == null || options.receiverAccountId().equals(accountActivity.getReceiverAccount().getId()))
                && (options.minimumAmount() == null || options.minimumAmount() <= accountActivity.getAmount())
                && (options.createAt() == null || (options.createAt().isEqual(accountActivity.getCreatedAt().toLocalDate())));

        List<AccountActivityDto> accountActivityDtos = new ArrayList<>();
        Comparator<AccountActivity> activityComparator = Comparator.comparing(AccountActivity::getCreatedAt).reversed();

        accountActivityRepository.findAll()
                .stream()
                .filter(transactionPredicate)
                .sorted(activityComparator)
                .forEach(accountActivity -> accountActivityDtos.add(accountActivityMapper.entityToDto(accountActivity)));

        return accountActivityDtos;
    }

    @Override
    public AccountActivityDto getAccountActivity(String id) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        AccountActivity accountActivity = findById(id);

        return accountActivityMapper.entityToDto(accountActivity);
    }

    @Override
    public void createAccountActivity(AccountActivityRequest request) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());

        if (request == null) {
            throw new ResourceNotFoundException("Account Activity request is not found");
        }

        AccountActivity accountActivity = new AccountActivity(
                request.activityType(),
                request.senderAccount(),
                request.receiverAccount(),
                request.amount(),
                request.explanation()
        );

        AccountActivity savedAccountActivity = accountActivityRepository.save(accountActivity);
        log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.ACCOUNT_ACTIVITY.getValue(), savedAccountActivity.getId());
    }

    @Override
    public List<AccountActivityView> getAccountActivityViews(Integer senderAccountId, Integer receiverAccountId) {
        log.info(LogMessages.ECHO, LoggingUtils.getCurrentClassName(), LoggingUtils.getCurrentMethodName());
        return accountActivityViewRepository.findBySenderAccountIdAndReceiverAccountId(senderAccountId, receiverAccountId);
    }

    private AccountActivity findById(String id) {
        String value = Entity.ACCOUNT_ACTIVITY.getValue();
        AccountActivity accountActivity = accountActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, value)));

        log.info(LogMessages.RESOURCE_FOUND, value);

        return accountActivity;
    }
}
