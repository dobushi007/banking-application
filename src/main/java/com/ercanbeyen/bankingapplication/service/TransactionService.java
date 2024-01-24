package com.ercanbeyen.bankingapplication.service;

import com.ercanbeyen.bankingapplication.dto.TransactionDto;
import com.ercanbeyen.bankingapplication.dto.request.TransactionRequest;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> getTransactions();
    TransactionDto getTransaction(String id);
    TransactionDto createTransaction(TransactionRequest request);
}
