package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.enums.Entity;
import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.dto.TransactionDto;
import com.ercanbeyen.bankingapplication.dto.request.TransactionRequest;
import com.ercanbeyen.bankingapplication.entity.Transaction;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.TransactionMapper;
import com.ercanbeyen.bankingapplication.option.TransactionFilteringOptions;
import com.ercanbeyen.bankingapplication.repository.TransactionRepository;
import com.ercanbeyen.bankingapplication.service.TransactionService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionDto> getTransactions(TransactionFilteringOptions options) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(TransactionServiceImpl.class.getEnclosingMethod()));

        Predicate<Transaction> transactionPredicate = transaction -> (options.type() == null || options.type() == transaction.getType())
                && (options.senderAccountId() == null || options.senderAccountId().equals(transaction.getSenderAccountId()))
                && (options.receiverAccountId() == null || options.receiverAccountId().equals(transaction.getReceiverAccountId()))
                && (options.minimumAmount() == null || options.minimumAmount() <= transaction.getAmount())
                && (options.createAt() == null || (options.createAt().isEqual(transaction.getCreateAt().toLocalDate())));

        List<TransactionDto> transactionDtoList = new ArrayList<>();
        Comparator<Transaction> transactionComparator = Comparator.comparing(Transaction::getCreateAt).reversed();

        transactionRepository.findAll()
                .stream()
                .filter(transactionPredicate)
                .sorted(transactionComparator)
                .forEach(transaction -> transactionDtoList.add(transactionMapper.transactionToDto(transaction)));

        return transactionDtoList;
    }

    @Override
    public TransactionDto getTransaction(String id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(TransactionServiceImpl.class.getEnclosingMethod()));

        Transaction transaction = findTransactionById(id);
        log.info(LogMessages.RESOURCE_FOUND, Entity.ACCOUNT);

        return transactionMapper.transactionToDto(transaction);
    }

    @Async
    @Override
    public void createTransaction(TransactionRequest request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(TransactionServiceImpl.class.getEnclosingMethod()));

        CompletableFuture.runAsync(() -> {
            Transaction transaction = new Transaction(
                    request.transactionType(),
                    request.senderAccountId(),
                    request.receiverAccountId(),
                    request.amount(),
                    request.explanation()
            );

            Transaction savedTransaction = transactionRepository.save(transaction);
            log.info(LogMessages.RESOURCE_CREATE_SUCCESS, Entity.TRANSACTION, savedTransaction.getId());
        });
    }

    private Transaction findTransactionById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Entity.TRANSACTION.getValue())));
    }
}
