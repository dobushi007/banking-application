package com.ercanbeyen.bankingapplication.service.impl;

import com.ercanbeyen.bankingapplication.constant.message.LogMessages;
import com.ercanbeyen.bankingapplication.constant.message.ResponseMessages;
import com.ercanbeyen.bankingapplication.constant.resource.Resources;
import com.ercanbeyen.bankingapplication.dto.TransactionDto;
import com.ercanbeyen.bankingapplication.dto.request.TransactionRequest;
import com.ercanbeyen.bankingapplication.entity.Transaction;
import com.ercanbeyen.bankingapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.bankingapplication.mapper.TransactionMapper;
import com.ercanbeyen.bankingapplication.repository.TransactionRepository;
import com.ercanbeyen.bankingapplication.service.TransactionService;
import com.ercanbeyen.bankingapplication.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionDto> getTransactions() {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        List<TransactionDto> transactionDtoList = new ArrayList<>();

        transactionRepository.findAll()
                .forEach(transaction -> transactionDtoList.add(transactionMapper.transactionToDto(transaction)));

        return transactionDtoList;
    }

    @Override
    public TransactionDto getTransaction(String id) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        Transaction transaction = findTransactionById(id);
        log.info(LogMessages.RESOURCE_FOUND, Resources.EntityNames.TRANSACTION);

        return transactionMapper.transactionToDto(transaction);
    }

    @Async
    @Override
    public void createTransaction(TransactionRequest request) {
        log.info(LogMessages.ECHO,
                LoggingUtils.getClassName(this),
                LoggingUtils.getMethodName(new Object() {}.getClass().getEnclosingMethod()));

        CompletableFuture.runAsync(() -> {
            Transaction transaction = new Transaction(
                    request.transactionType(),
                    request.senderAccount(),
                    request.receiverAccount(),
                    request.amount(),
                    LocalDateTime.now(),
                    request.explanation()
            );

            Transaction savedTransaction = transactionRepository.save(transaction);
            log.info("Transaction {} is successfully created", savedTransaction.getType());
        });
    }

    private Transaction findTransactionById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, Resources.EntityNames.TRANSACTION)));
    }
}
