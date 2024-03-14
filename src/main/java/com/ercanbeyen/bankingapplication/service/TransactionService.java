package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.TransactionDto;
import com.ercanbeyen.bankingapplication.dto.request.TransactionRequest;
import com.ercanbeyen.bankingapplication.entity.TransactionView;
import com.ercanbeyen.bankingapplication.option.TransactionFilteringOptions;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> getTransactions(TransactionFilteringOptions options);
    TransactionDto getTransaction(String id);
    void createTransaction(TransactionRequest request);
    List<TransactionView> getTransactions(Integer senderAccountId, Integer receiverAccountId);
}
